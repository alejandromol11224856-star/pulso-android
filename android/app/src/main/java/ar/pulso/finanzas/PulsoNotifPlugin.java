package ar.pulso.finanzas;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

/** Puente entre la web de Pulso y el lector de avisos. */
@CapacitorPlugin(name = "PulsoNotif")
public class PulsoNotifPlugin extends Plugin {
    private static PulsoNotifPlugin instance;

    @Override
    public void load() { instance = this; }

    /** Avisa a la página si está abierta (si no, el aviso queda en la cola). */
    static void emit(JSONObject item) {
        PulsoNotifPlugin p = instance;
        if (p == null) return;
        try { p.notifyListeners("aviso", JSObject.fromJSONObject(item)); } catch (Exception ignored) { }
    }

    @PluginMethod
    public void isEnabled(PluginCall call) {
        String flat = Settings.Secure.getString(getContext().getContentResolver(), "enabled_notification_listeners");
        ComponentName me = new ComponentName(getContext(), PulsoNotificationService.class);
        boolean on = flat != null && (flat.contains(me.flattenToString()) || flat.contains(me.flattenToShortString()));
        JSObject r = new JSObject();
        r.put("enabled", on);
        call.resolve(r);
    }

    @PluginMethod
    public void openSettings(PluginCall call) {
        Intent i = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }

    /** Devuelve los avisos guardados y vacía la cola. */
    @PluginMethod
    public void take(PluginCall call) {
        try {
            JSONArray q = PulsoNotificationService.take(getContext());
            JSObject r = new JSObject();
            r.put("items", new JSArray(q.toString()));
            call.resolve(r);
        } catch (Exception e) {
            call.reject("No pude leer los avisos", e);
        }
    }
}
