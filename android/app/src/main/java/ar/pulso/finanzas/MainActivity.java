package ar.pulso.finanzas;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(PulsoNotifPlugin.class); // lector de avisos de bancos (carga automática)
        super.onCreate(savedInstanceState);
    }
}
