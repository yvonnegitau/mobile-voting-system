/*
 * © 2010, Jakub Valenta
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Jakub Valenta
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors “as is” and any
 * express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the foundation or contributors be liable for any direct, indirect,
 * incidental, special, exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits; or business
 * interruption) however caused and on any theory of liability, whether in contract, strict
 * liability, or tort (including negligence or otherwise) arising in any way out of the use
 * of this software, even if advised of the possibility of such damage.
 */
package cz.cvut.fel.mvod.persistence.derby;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Connection to Derby embedded database.
 * @author jakub
 */
class DerbySqlConnection {

	private Connection dbConnection;
	private Properties dbProperties;
	private boolean isConnected;
	private String dbName = "DataStore";

	private static DerbySqlConnection instance = null;

	private DerbySqlConnection() {
		setDBSystemDir();
		dbProperties = loadDBProperties();
		String driverName = dbProperties.getProperty("derby.driver");
		loadDatabaseDriver(driverName);
	}

	private DerbySqlConnection(String name) {
		setDBSystemDir();
		dbProperties = loadDBProperties();
		dbName = name;
		String driverName = dbProperties.getProperty("derby.driver");
		loadDatabaseDriver(driverName);
	}

	/**
	 * Returns the only instance of <code>DerbySqlConnection</code>.
	 * @return <code>DerbySqlConnection</code>
	 */
	public static DerbySqlConnection getInstance() {
		if(instance == null) {
			instance = new DerbySqlConnection();
		}
		return instance;
	}

	/**
	 * Returns the only instance of <code>DerbySqlConnection</code>.
	 * @param name of the database
	 * @return <code>DerbySqlConnection</code>
	 * @throws IllegalArgumentException if is already established connection to database
	 * with another name
	 */
	static DerbySqlConnection getInstance(String name) {
		if(instance != null && !instance.dbName.equals(name)) {
			throw new IllegalArgumentException("Database name is not equal " +
					"to name of currently connected database.");
		}
		if(instance == null) {
			instance = new DerbySqlConnection(name);
		}
		return instance;
	}

	/**
	 * Returns system dependent path to database.
	 * @return system dependent path to database.
	 */
	public String getDatabaseLocation() {
		String dbLocation = System.getProperty("derby.system.home") + "/" + dbName;
		return dbLocation;
	}

	/**
	 * Returns URI to database. It may be used to connect the database.
	 * @return URI to database
	 */
    public String getDatabaseUrl() {
        String dbUrl = dbProperties.getProperty("derby.url") + dbName;
        return dbUrl;
    }

	/**
	 * Establish connection to database. This method must be called before
	 * calling  {@link #disconnect()} and {@link #prepareStatement(java.lang.String)}
	 * methods.
	 * @throws DerbyDatabaseException
	 */
	public void connect() throws DerbyDatabaseException {
        if(!isConnected) {
			String dbUrl = getDatabaseUrl();
			if(!dbExists()) {
				dbProperties.put("create", "true");
				try {
					dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
					isConnected = dbConnection != null;
				} catch(SQLException ex) {
					throw new DerbyDatabaseException(ex);
				}
				createTables();
				dbProperties.remove("create");
			} else {
				try {
					dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
					isConnected = dbConnection != null;
				} catch (SQLException ex) {
					isConnected = false;
					throw new DerbyDatabaseException(ex);
				}
			}
		}
    }

	/**
	 * Creates precompiled statement for faster query execution.
	 * @param query to compile
	 * @return precompiled sql statement
	 * @throws DerbyDatabaseException if database is not connected or statement contains error
	 */
	public PreparedStatement prepareStatement(String query) throws DerbyDatabaseException {
		if(isConnected) {
			try {
				return dbConnection.prepareStatement(query,
						PreparedStatement.RETURN_GENERATED_KEYS);
			} catch(SQLException ex) {
				throw new DerbyDatabaseException(ex);
			}
		}
		throw new DerbyDatabaseException("Database is not connected.");
	}

	/**
	 * Closes connection to database.
	 */
    public void disconnect() {
        if(isConnected) {
//TODO control needed
            String dbUrl = getDatabaseUrl();
            dbProperties.put("shutdown", "true");
            try {
                DriverManager.getConnection(dbUrl, dbProperties);
            } catch (SQLException ex) {
				//ok
            }
            isConnected = false;
        }
    }

	private void createTables() throws DerbyDatabaseException {
		Statement statement = null;
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(
					DerbySqlConnection.class.getResourceAsStream("Schema.sql")));
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line = r.readLine()) != null) {
				buffer.append(line);
			}
			String[] queries = buffer.toString().split(";", -1);
			for(int i = 0; i < queries.length - 1; i ++) {
				statement = dbConnection.createStatement();
				statement.execute(queries[i]);
			}
		} catch(Exception ex) {
			throw new DerbyDatabaseException(ex);
		} finally {
			if(r != null) {
				try {
					r.close();
				} catch(IOException ex) {
					//ok
				}
			}
		}
	}

	private void setDBSystemDir() {
		// decide on the db system directory
		String userHomeDir = System.getProperty("user.home", ".");
		String systemDir = userHomeDir + "/.mvod/db";
		System.setProperty("derby.system.home", systemDir);
		// create the db system directory
		File fileSystemDir = new File(systemDir);
		fileSystemDir.mkdir();
	}

	private boolean dbExists() {
		String dbLocation = getDatabaseLocation();
		File dbFileDir = new File(dbLocation);
		return dbFileDir.exists();
	}

	private Properties loadDBProperties() {
		InputStream dbPropInputStream = null;
		try {
			dbPropInputStream = DerbySqlConnection.class.getResourceAsStream("Derby.properties");
			dbProperties = new Properties();
			dbProperties.load(dbPropInputStream);
		} catch(IOException ex) {
			ex.printStackTrace();
			return null;
			//TODO properties does not exists
		} finally {
			if(dbPropInputStream != null) {
				try {
					dbPropInputStream.close();
				} catch(IOException ex) {
					//ok
				}
			}
		}
		return dbProperties;
	}

	private void loadDatabaseDriver(String driverName) {
		try {
			Class.forName(driverName);
		} catch(ClassNotFoundException ex) {
			ex.printStackTrace();
			//ok
		}
	}

}
