package es.udc.tfg.pruebafinalfirebase.Core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import es.udc.tfg.pruebafinalfirebase.Core.mService;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context,  mService.class);
        context.startService(service);
    }
}
