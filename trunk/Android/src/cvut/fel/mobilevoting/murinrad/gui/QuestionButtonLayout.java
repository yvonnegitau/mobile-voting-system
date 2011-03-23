package cvut.fel.mobilevoting.murinrad.gui;

import cvut.fel.mobilevoting.murinrad.R;
import cvut.fel.mobilevoting.murinrad.datacontainers.QuestionData;
import cvut.fel.mobilevoting.murinrad.views.QuestionsView;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class QuestionButtonLayout extends LinearLayout {
	QuestionButton qb = null;
	ImageView checker = null;
	boolean checked = false;

	public QuestionButtonLayout(final Context context,
			final QuestionData qData, final QuestionsView parent) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		qb = new QuestionButton(context, qData, parent);
		qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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

	public void showCheckers() {
		//invalidate();
		checker.setVisibility(VISIBLE);
		checker.invalidate();
		qb.setLayoutParams(new LayoutParams(280, LayoutParams.WRAP_CONTENT));
		//updateViewLayout(qb, new LayoutParams(220, LayoutParams.WRAP_CONTENT));
		//requestLayout();
		
		
	}

	public void hideCheckers() {
		checker.setVisibility(GONE);
		qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		checker.invalidate();
		//invalidate();
		//checker.setVisibility(GONE);
		//qb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		 //requestLayout();
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public QuestionData extractQData() {
		return qb.extractQData();
	}

}
