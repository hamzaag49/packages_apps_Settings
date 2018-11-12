/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;

import com.android.internal.app.ColorDisplayController;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.NightDisplaySettings;
import com.android.settings.homepage.contextualcards.ContextualCard;

import java.util.Objects;

public class NightDisplayConditionController implements ConditionalCardController,
        ColorDisplayController.Callback {
    static final int ID = Objects.hash("NightDisplayConditionController");

    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final ColorDisplayController mController;

    public NightDisplayConditionController(Context appContext, ConditionManager manager) {
        mController = new ColorDisplayController(appContext);
        mAppContext = appContext;
        mConditionManager = manager;
    }

    @Override
    public long getId() {
        return ID;
    }

    @Override
    public boolean isDisplayable() {
        return mController.isActivated();
    }

    @Override
    public void onPrimaryClick(Context context) {
        new SubSettingLauncher(context)
                .setDestination(NightDisplaySettings.class.getName())
                .setSourceMetricsCategory(MetricsProto.MetricsEvent.SETTINGS_HOMEPAGE)
                .setTitleRes(R.string.night_display_title)
                .launch();
    }

    @Override
    public void onActionClick() {
        mController.setActivated(false);
    }

    @Override
    public ContextualCard buildContextualCard() {
        return new ConditionalContextualCard.Builder()
                .setConditionId(ID)
                .setMetricsConstant(MetricsProto.MetricsEvent.SETTINGS_CONDITION_NIGHT_DISPLAY)
                .setActionText(mAppContext.getText(R.string.condition_turn_off))
                .setName(mAppContext.getPackageName() + "/"
                        + mAppContext.getText(R.string.condition_night_display_title))
                .setTitleText(mAppContext.getText(
                        R.string.condition_night_display_title).toString())
                .setSummaryText(
                        mAppContext.getText(R.string.condition_night_display_summary).toString())
                .setIconDrawable(mAppContext.getDrawable(R.drawable.ic_settings_night_display))
                .setIsHalfWidth(true)
                .build();
    }

    @Override
    public void startMonitoringStateChange() {
        mController.setListener(this);
    }

    @Override
    public void stopMonitoringStateChange() {
        mController.setListener(null);
    }

    @Override
    public void onActivated(boolean activated) {
        mConditionManager.onConditionChanged();
    }
}