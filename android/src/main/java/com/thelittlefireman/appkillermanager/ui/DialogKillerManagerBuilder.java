package com.thelittlefireman.appkillermanager.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.thelittlefireman.appkillermanager.managers.KillerManager;
import com.thelittlefireman.appkillermanager.utils.KillerManagerUtils;
import com.thelittlefireman.appkillermanager.utils.LogUtils;

import in.jvapps.disable_battery_optimization.R;

public class DialogKillerManagerBuilder {
    private Context mContext;

    public DialogKillerManagerBuilder() {
        contentResMessage = -1;
        titleResMessage = -1;
        iconRes = -1;
    }

    public DialogKillerManagerBuilder(Context context) {
        this();
        mContext = context;
    }

    private KillerManager.Actions mAction;

    private boolean enableDontShowAgain = true;

    private String titleMessage;
    private String contentMessage;

    private String positiveBtnStr;
    private String negativeBtnStr;

    private DialogInterface.OnClickListener onPositive;
    private DialogInterface.OnClickListener onNegative;

    @DrawableRes
    private int iconRes;

    @StringRes
    private int titleResMessage, contentResMessage;

    public DialogKillerManagerBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    public DialogKillerManagerBuilder setAction(KillerManager.Actions action) {
        mAction = action;
        return this;
    }

    public DialogKillerManagerBuilder setIconRes(@NonNull @DrawableRes int iconRes) {
        this.iconRes = iconRes;
        return this;
    }

    public DialogKillerManagerBuilder setDontShowAgain(boolean enable) {
        this.enableDontShowAgain = enable;
        return this;
    }

    public DialogKillerManagerBuilder setTitleMessage(@NonNull String titleMessage) {
        this.titleMessage = titleMessage;
        return this;
    }

    public DialogKillerManagerBuilder setContentMessage(@NonNull String contentMessage) {
        this.contentMessage = contentMessage;
        return this;
    }

    public DialogKillerManagerBuilder setTitleMessage(@StringRes @NonNull int titleResMessage) {
        this.titleResMessage = titleResMessage;
        return this;
    }

    public DialogKillerManagerBuilder setContentMessage(@StringRes @NonNull int contentResMessage) {
        this.contentResMessage = contentResMessage;
        return this;
    }

    public DialogKillerManagerBuilder setPositiveMessage(@NonNull String positiveMessage) {
        this.positiveBtnStr = positiveMessage;
        return this;
    }

    public DialogKillerManagerBuilder setNegativeMessage(@NonNull String negativeMessage) {
        this.negativeBtnStr = negativeMessage;
        return this;
    }

    public DialogKillerManagerBuilder setOnPositiveCallback(@NonNull DialogInterface.OnClickListener onPositive) {
        this.onPositive = onPositive;
        return this;
    }

    public DialogKillerManagerBuilder setOnNegativeCallback(@NonNull DialogInterface.OnClickListener onNegative) {
        this.onNegative = onNegative;
        return this;
    }

    public void show() {

        AlertDialog materialDialog;
        if (mContext == null) {
            throw new NullPointerException("Context can't be null");
        }
        if (mAction == null) {
            throw new NullPointerException("Action can't be null");
        }
        KillerManager.init(mContext);

        if (!KillerManager.isActionAvailable(mContext, mAction)) {
            LogUtils.i(this.getClass().getName(), "This action is not available for this device no need to show the dialog");
            return;
        }

        if (KillerManager.getDevice() == null) {
            LogUtils.i(this.getClass().getName(), "Device not in the list no need to show the dialog");
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
        if (positiveBtnStr == null) {
            positiveBtnStr = mContext.getText(R.string.dialog_button).toString();
        }
        if (negativeBtnStr == null) {
            negativeBtnStr = mContext.getText(android.R.string.cancel).toString();
        }

        builder.setPositiveButton(positiveBtnStr, (dialog, which) -> {
            KillerManager.doAction(mContext, mAction);
            if (onPositive != null) {
                onPositive.onClick(dialog, which);
            }
        }).setView(R.layout.md_dialog_custom_view)
                .setNegativeButton(negativeBtnStr, (dialog, which) -> {
                    KillerManager.doAction(mContext, mAction);
                    if (onNegative != null) {
                        onNegative.onClick(dialog, which);
                    }
                });

        if (iconRes != -1) {
            builder.setIcon(iconRes);
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_alert);
        }

        if (titleResMessage != -1) {
            builder.setTitle(titleResMessage);
        } else if (titleMessage != null && !titleMessage.isEmpty()) {
            builder.setTitle(titleMessage);
        } else {
            builder.setTitle(mContext.getString(R.string.dialog_title_notification, KillerManager.getDevice().getDeviceManufacturer().toString()));
        }

        if (this.enableDontShowAgain) {
            CharSequence[] items = {mContext.getText(R.string.dialog_do_not_show_again), mContext.getText(R.string.remind_me_again)};
            int checkedItem = R.string.remind_me_again;
            builder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
                KillerManagerUtils.setDontShowAgain(mContext, mAction, checkedItem == R.string.dialog_do_not_show_again);
            });
        }

        if (!(enableDontShowAgain && KillerManagerUtils.isDontShowAgain(mContext, mAction))) {
            materialDialog = builder.show();

            // init custom view
            assert materialDialog.getCurrentFocus() != null;
            initView(materialDialog.getCurrentFocus());
        }
    }

    private void initView(View view) {
        TextView contentTextView = view.findViewById(R.id.md_content);
        CheckBox doNotShowAgainCheckBox = view.findViewById(R.id.md_promptCheckbox);
        ImageView helpImageView = view.findViewById(R.id.md_imageView);

        if (contentResMessage != -1) {
            contentTextView.setText(contentResMessage);
        } else if (contentMessage != null && !contentMessage.isEmpty()) {
            contentTextView.setText(contentMessage);
        } else {
            //TODO CUSTOM MESSAGE FOR SPECIFITQUE ACTIONS AND SPECIFIC DEVICE
            contentTextView.setText(String.format(mContext.getString(R.string.dialog_huawei_notification),
                    mContext.getString(
                            R.string.app_name)));
        }

        if (this.enableDontShowAgain) {
            doNotShowAgainCheckBox.setVisibility(View.VISIBLE);
            doNotShowAgainCheckBox.setText(R.string.dialog_do_not_show_again);
            doNotShowAgainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    KillerManagerUtils.setDontShowAgain(mContext, mAction, isChecked);
                }
            });
        }

        //TODO add other specific images
        int helpImageRes = 0;
        switch (mAction) {
            case ACTION_AUTOSTART:
                helpImageRes = KillerManager.getDevice().getHelpImageAutoStart();
                break;
            case ACTION_POWERSAVING:
                helpImageRes = KillerManager.getDevice().getHelpImagePowerSaving();
                break;
            case ACTION_NOTIFICATIONS:
                helpImageRes = KillerManager.getDevice().getHelpImageNotification();
                break;
        }

        if (helpImageRes != 0) {
            helpImageView.setImageResource(helpImageRes);
        } else {
            helpImageView.setVisibility(View.GONE);
        }
    }
}