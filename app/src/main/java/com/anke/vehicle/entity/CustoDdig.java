package com.anke.vehicle.entity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.anke.vehicle.R;

import java.util.List;

public class CustoDdig extends Dialog {

	public CustoDdig(Context context) {
		super(context);
	}

	public CustoDdig(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private List<String> listcoant;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		public String stationName; // 修改的分站名称
		private GJAdapter gja; // 自定义适配器 2016-5-09 xmx

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(List<String> listcoant) {
			this.listcoant = listcoant;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param title
		 * @return
		 */
		// public Builder setMessage(int[] str1) {
		// for(int i = 0; i < str1.length ; i ++)
		// this.str1[i] = (String) context.getText(str1[i]);
		// return this;
		// }

		/**
		 * Set the Dialog title from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 *
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustoDdig create() {
			gja = new GJAdapter(); // 给适配器传参，用于下拉框的点击事件 2015-11-05 肖明星
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustoDdig dialog = new CustoDdig(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialogstyle, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.tvdtitle)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (listcoant != null) {
				((ListView) layout.findViewById(R.id.lvdig)).setAdapter(gja);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
//				((LinearLayout) layout.findViewById(R.id.content))
//						.removeAllViews();
//				((LinearLayout) layout.findViewById(R.id.content)).addView(
//						contentView, new LayoutParams(LayoutParams.FILL_PARENT,
//								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

		// 适配器 2015-11-06 肖明星
		class GJAdapter extends BaseAdapter {
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return listcoant.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return listcoant.get(arg0);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(final int position, View convertView,
								ViewGroup parent) {
				// TODO Auto-generated method stub
				if (convertView == null) { // 这句就是重用的关键
					convertView = LayoutInflater.from(context).inflate(
							R.layout.dialogstyleitem, null);
				}
				// final RelativeLayout = (RelativeLayout)
				// convertView.findViewById(R.id.rldialog);
				final RadioButton tvtitle = (RadioButton) convertView
						.findViewById(R.id.rdDialog);
				final String title = listcoant.get(position);
				tvtitle.setText(title);
				convertView.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						stationName = tvtitle.getText().toString();
					}
				});
				return convertView;
			}
		}
	}
}
