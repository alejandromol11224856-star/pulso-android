package ar.pulso.finanzas;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Lee los avisos de bancos y billeteras (Brubank, Mercado Pago, Ualá…) para que Pulso cargue los pagos solo.
 * Solo mira las apps de la lista; guarda el texto en este celular hasta que Pulso lo procesa. No manda nada a internet.
 */
public class PulsoNotificationService extends NotificationListenerService {
    static final String PREFS = "pulso_avisos";
    static final String KEY = "cola";
    private static final int MAX = 200;

    /** Partes del nombre de paquete de las apps de bancos/billeteras que se leen. */
    private static final String[] APPS = {
        "brubank", "mercadopago", "uala", "naranja", "galicia", "santander", "bbva", "macro", "icbc",
        "bapro", "cuentadni", "bna", "lemon", "belo", "prex", "personalpay", "playdigital.modo", "cocos",
        "patagonia", "supervielle", "hsbc", "bancociudad"
    };

    static boolean watched(String pkg, boolean debug) {
        if (pkg == null) return false;
        String p = pkg.toLowerCase();
        if (debug && p.equals("com.android.shell")) return true; // para probar con adb
        for (String a : APPS) if (p.contains(a)) return true;
        return false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            boolean debug = (getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            if (!watched(sbn.getPackageName(), debug)) return;
            Notification n = sbn.getNotification();
            if ((n.flags & Notification.FLAG_GROUP_SUMMARY) != 0) return;
            Bundle ex = n.extras;
            String title = str(ex.getCharSequence(Notification.EXTRA_TITLE));
            String text = str(ex.getCharSequence(Notification.EXTRA_BIG_TEXT));
            if (text.isEmpty()) text = str(ex.getCharSequence(Notification.EXTRA_TEXT));
            if (text.isEmpty() && title.isEmpty()) return;

            JSONObject item = new JSONObject();
            item.put("id", sbn.getPackageName() + "|" + (title + "|" + text).hashCode());
            item.put("pkg", sbn.getPackageName());
            item.put("title", title);
            item.put("text", text);
            item.put("time", sbn.getPostTime());
            add(getApplicationContext(), item);
            PulsoNotifPlugin.emit(item);
        } catch (Exception ignored) { }
    }

    private static String str(CharSequence c) { return c == null ? "" : c.toString().trim(); }

    static synchronized void add(Context ctx, JSONObject item) throws Exception {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        JSONArray q = new JSONArray(sp.getString(KEY, "[]"));
        String id = item.getString("id");
        for (int i = 0; i < q.length(); i++) if (id.equals(q.getJSONObject(i).optString("id"))) return; // ya estaba
        q.put(item);
        while (q.length() > MAX) q.remove(0);
        sp.edit().putString(KEY, q.toString()).apply();
    }

    static synchronized JSONArray take(Context ctx) throws Exception {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        JSONArray q = new JSONArray(sp.getString(KEY, "[]"));
        sp.edit().putString(KEY, "[]").apply();
        return q;
    }
}
