/*
Copyright 2011 Radovan Murin

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * The container that houses a question button, it enables the display of
 * checkers
 * 
 * @author Radovan Murin
 * 
 */
public class QuestionButtonLayout extends LinearLayout {
	QuestionButton qb = null;
	ImageView checker = null;
	boolean checked = false;
/**
 * The constructor for the layout
 * @param context the context of the application
 * @param qData the qData to be displayed in a button
 * @param parent the parent view
 */
	public QuestionButtonLayout(final Context context,
			final QuestionData qData, final QuestionsView parent) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		qb = new QuestionButton(context, qData, parent);
		qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		addView(qb, 0);

		checker = new ImageView(context);
		checker.setImageResource(R.drawable.notselected);
		checker.setLayoutParams(new LayoutParams(40, 40));
		checker.setClickable(true);
		checker.setVisibility(GONE);
		checker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checked) {
					checker.setImageResource(R.drawable.selected);
					checked = true;
				} else {
					checker.setImageResource(R.drawable.notselected);
					checked = false;
				}

			}
		});
		addView(checker, 1);

	}

	/**
	 * Shows checkers, a checker is essentially a button that when checked the
	 * answer will be sent in the next batch
	 */
	public void showCheckers() {
		// invalidate();
		checker.setVisibility(VISIBLE);
		checker.invalidate();
		qb.setLayoutParams(new LayoutParams(280, LayoutParams.WRAP_CONTENT));
		// updateViewLayout(qb, new LayoutParams(220,
		// LayoutParams.WRAP_CONTENT));
		// requestLayout();

	}

	/**
	 * Hides the checkers
	 */
	public void hideCheckers() {
		checker.setVisibility(GONE);
		qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		checker.invalidate();
		// invalidate();
		// checker.setVisibility(GONE);
		// qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// requestLayout();
	}

	public boolean isChecked() {
		return checked;
	}
/**
 * Returns the question data of this layout
 * @return the question data in its class envelope
 */
	public QuestionData extractQData() {
		return qb.extractQData();
	}

}
