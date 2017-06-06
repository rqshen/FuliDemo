package com.bcb.util;

import android.content.Context;
import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class PasswordEditText extends EditText {
	public PasswordEditText(Context ctx) {
		super(ctx);
	}
	public PasswordEditText(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		setTransformationMethod(new PasswordInputType());
	}
	
	class PasswordInputType implements TransformationMethod {

		@Override
		public CharSequence getTransformation(CharSequence source, View view) {
			return new PasswordCharSequence(source);
		}

		@Override
		public void onFocusChanged(View view, CharSequence sourceText,
			boolean focused, int direction, Rect previouslyFocusedRect) {
		}
		
		class PasswordCharSequence implements CharSequence{
			CharSequence charSequence;
			
			public PasswordCharSequence(CharSequence charSequence) {
				this.charSequence = charSequence;
			}
			@Override
			public int length() {
				return charSequence.length();
			}

			@Override
			public char charAt(int index) {
				return '*';
			}

			@Override
			public CharSequence subSequence(int start, int end) {
				return charSequence.subSequence(start, end);
			}
		}
	}
}
