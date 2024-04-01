package com.bitbytestudio.prayerapp.widget

//import android.app.PendingIntent
//import android.appwidget.AppWidgetManager
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.widget.RemoteViews
//import com.bitbytestudio.prayerapp.R
//import com.bitbytestudio.prayerapp.ui.MainActivity
//import com.bitbytestudio.prayerapp.utils.Constants.CHECK_DAY
//import com.bitbytestudio.prayerapp.utils.Constants.COLOR
//import com.bitbytestudio.prayerapp.utils.Constants.RESET_SALAVAT
//import com.bitbytestudio.prayerapp.utils.Constants.SALAVAT
//import com.bitbytestudio.prayerapp.utils.DateManager
//import com.bitbytestudio.prayerapp.utils.HawkManager
//
//class SalavatWidget : BaseWidget() {
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                                     overrides                                              //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    override fun onAfterUpdate(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetIds: IntArray
//    ) {
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(
//                context = context,
//                appWidgetManager = appWidgetManager,
//                appWidgetId = appWidgetId
//            )
//        }
//    }
//
//    override fun onAfterOptionChanged(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetId: Int
//    ) {
//        updateAppWidget(
//            context = context,
//            appWidgetManager = appWidgetManager,
//            appWidgetId = appWidgetId
//        )
//    }
//
//    override fun onAfterReceive(context: Context, intent: Intent) {
//        val remoteViews = RemoteViews(context.packageName, R.layout.widget_salavat)
//        val hawkManager = HawkManager(context)
//        when (intent.action) {
//            SALAVAT -> {
//                val todayName = DateManager.getTodayName()
//                val savedDay = hawkManager.getSalavatDay()
//                if (todayName != savedDay) {
//                    hawkManager.saveSalavat(salavat = 0)
//                    hawkManager.saveSalavatDay(day = DateManager.getTodayName())
//                    remoteViews.setTextViewText(
//                        R.id.tvSalavatCounter,
//                        hawkManager.getSalavat().toString()
//                    )
//                    remoteViews.setTextViewText(R.id.tvSalavatDay, DateManager.getTodayName())
//                } else {
//                    remoteViews.setTextViewText(
//                        R.id.tvSalavatCounter,
//                        hawkManager.increaseSalavat().toString()
//                    )
//                }
//                AppWidgetManager.getInstance(context).updateAppWidget(
//                    ComponentName(context, SalavatWidget::class.java),
//                    remoteViews
//                )
//            }
//
//            RESET_SALAVAT -> {
//                remoteViews.setTextViewText(
//                    R.id.tvSalavatCounter,
//                    hawkManager.getSalavat().toString()
//                )
//                AppWidgetManager.getInstance(context).updateAppWidget(
//                    ComponentName(context, SalavatWidget::class.java),
//                    remoteViews
//                )
//            }
//
//            COLOR -> {
//                remoteViews.setTextColor(
//                    R.id.tvSalavatTitle,
//                    context.resources.getColor(hawkManager.getTextColor().color)
//                )
//                AppWidgetManager.getInstance(context).updateAppWidget(
//                    ComponentName(context, SalavatWidget::class.java),
//                    remoteViews
//                )
//            }
//
//            CHECK_DAY -> {
//                val todayName = DateManager.getTodayName()
//                val savedDay = hawkManager.getSalavatDay()
//                if (todayName != savedDay) {
//                    hawkManager.saveSalavat(salavat = 0)
//                    hawkManager.saveSalavatDay(day = DateManager.getTodayName())
//                    remoteViews.setTextViewText(
//                        R.id.tvSalavatCounter,
//                        hawkManager.getSalavat().toString()
//                    )
//                    remoteViews.setTextViewText(R.id.tvSalavatDay, DateManager.getTodayName())
//                    AppWidgetManager.getInstance(context).updateAppWidget(
//                        ComponentName(context, SalavatWidget::class.java),
//                        remoteViews
//                    )
//                }
//            }
//        }
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                                       configs                                              //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * initialize the widget content or change them.
//     */
//    private fun updateAppWidget(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetId: Int
//    ) {
//        val views = RemoteViews(context.packageName, R.layout.widget_salavat)
//        val hawkManager = HawkManager(context)
//        hawkManager.saveSalavatDay(day = DateManager.getTodayName())
//        views.setTextViewText(R.id.tvSalavatDay, DateManager.getTodayName())
//        views.setTextViewText(R.id.tvSalavatCounter, hawkManager.getSalavat().toString())
//        views.setTextColor(
//            R.id.tvSalavatTitle,
//            context.resources.getColor(hawkManager.getTextColor().color)
//        )
//        views.setOnClickPendingIntent(
//            R.id.tvSalavatCounter,
//            updateSalavatIntent(
//                context = context,
//                action = SALAVAT
//            )
//        )
//        views.setOnClickPendingIntent(
//            R.id.tvSalavatDay,
//            openHomeActivityIntent(
//                context = context,
//                appWidgetId = appWidgetId
//            )
//        )
//        appWidgetManager.updateAppWidget(appWidgetId, views)
//    }
//
//    /**
//     * generate the specific pending intent to open home activity then return it.
//     */
//    private fun openHomeActivityIntent(
//        context: Context,
//        appWidgetId: Int
//    ): PendingIntent? {
//        val intent = Intent(context, MainActivity::class.java)
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
//        return PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//
//    /**
//     * generate the specific pending intent to update salavat counter then return it.
//     */
//    private fun updateSalavatIntent(
//        context: Context?,
//        action: String?
//    ): PendingIntent? {
//        val intent = Intent(context, SalavatWidget::class.java)
//        intent.action = action
//        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//    }
//
//}