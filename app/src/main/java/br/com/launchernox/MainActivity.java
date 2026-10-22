package br.com.launchernox;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, content;
    private int accent = Color.rgb(155, 120, 255);
    private boolean light = false;
    private SharedPreferences prefs;
    private final List<ResolveInfo> apps = new ArrayList<>();
    private EditText search;
    private int bg() { return light ? Color.rgb(245,245,250) : Color.rgb(15,16,23); }
    private int fg() { return light ? Color.rgb(25,25,35) : Color.WHITE; }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("theme", MODE_PRIVATE);
        light = prefs.getBoolean("light", false);
        accent = prefs.getInt("accent", Color.rgb(155,120,255));
        showHome();
    }
    private TextView label(String text, int size, boolean bold) {
        TextView v = new TextView(this); v.setText(text); v.setTextSize(size);
        v.setTextColor(fg()); if (bold) v.setTypeface(null, 1);
        v.setGravity(Gravity.CENTER_VERTICAL); v.setPadding(8,8,8,8); return v;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(20,18,20,16); l.setBackgroundColor(bg()); return l;
    }
    private TextView button(String text, Runnable action) {
        TextView v = label(text, 15, true); v.setGravity(Gravity.CENTER);
        v.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
        v.setBackground(round(accent)); v.setPadding(12,13,12,13);
        v.setOnClickListener(w -> action.run()); return v;
    }
    private android.graphics.drawable.Drawable round(int color) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color); d.setCornerRadius(28); return d;
    }
    private void base(String title) {
        root = column(); setContentView(root);
        TextView t = label(title, 27, true); t.setPadding(4,10,4,18); root.addView(t);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        ScrollView sc = new ScrollView(this); sc.setFillViewport(true); sc.addView(content);
        root.addView(sc, new LinearLayout.LayoutParams(-1,0,1));
    }
    private void add(LinearLayout parent, View child) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = 10; parent.addView(child,p);
    }
    private void showHome() {
        base("Nox");
        TextView clock = label(new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new java.util.Date()), 58, true);
        clock.setGravity(Gravity.CENTER); content.addView(clock);
        TextView date = label(new java.text.SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new java.util.Date()), 16, false);
        date.setGravity(Gravity.CENTER); content.addView(date);
        add(content, label(" ",12,false));
        search = new EditText(this); search.setSingleLine(true); search.setTextColor(fg());
        search.setHintTextColor(light?Color.GRAY:0xffaaaaaa); search.setHint("⌕  Pesquisar aplicativos");
        search.setBackground(round(light?0xffe5e5ed:0xff292a35)); search.setPadding(18,10,18,10);
        search.setOnEditorActionListener((v,a,e)->{showDrawer(search.getText().toString());return true;});
        add(content, search);
        add(content, button("⌕   Pesquisar apps", () -> showDrawer(search.getText().toString())));
        add(content, button("▦   Todos os aplicativos", () -> showDrawer("")));
        add(content, button("⚙   Personalizar", this::showSettings));
        add(content, label("Seus aplicativos",18,true));
        loadApps();
        int shown=0;
        for (ResolveInfo ri: apps) {
            if (shown++ >= 4) break;
            add(content, appRow(ri));
        }
        TextView hint = label("Dica: defina Nox como app de início padrão nas configurações do Android.",12,false);
        hint.setGravity(Gravity.CENTER); content.addView(hint);
    }
    private void loadApps() {
        apps.clear();
        Intent i = new Intent(Intent.ACTION_MAIN, null); i.addCategory(Intent.CATEGORY_LAUNCHER);
        apps.addAll(getPackageManager().queryIntentActivities(i,0));
        Collections.sort(apps, Comparator.comparing(a -> a.loadLabel(getPackageManager()).toString().toLowerCase(Locale.ROOT)));
    }
    private View appRow(ResolveInfo ri) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(10,8,10,8);
        row.setBackground(round(light?0xffeeeeF5:0xff22232e));
        TextView icon = new TextView(this); Drawable d=ri.loadIcon(getPackageManager());
        icon.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null); icon.setText("   "+ri.loadLabel(getPackageManager()));
        icon.setTextColor(fg()); icon.setTextSize(16); icon.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(icon,new LinearLayout.LayoutParams(-1,56));
        row.setOnClickListener(v -> {
            try { Intent launch=getPackageManager().getLaunchIntentForPackage(ri.activityInfo.packageName); if(launch!=null) startActivity(launch); }
            catch(Exception e){Toast.makeText(this,"Não foi possível abrir o app",Toast.LENGTH_SHORT).show();}
        });
        return row;
    }
    private void showDrawer(String query) {
        base("Aplicativos");
        EditText filter = new EditText(this); filter.setSingleLine(true); filter.setTextColor(fg());
        filter.setHint("Pesquisar pelo nome"); filter.setHintTextColor(Color.GRAY);
        filter.setText(query); filter.setBackground(round(light?0xffe5e5ed:0xff292a35)); filter.setPadding(14,8,14,8);
        add(content,filter);
        LinearLayout list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); content.addView(list);
        Runnable refresh = () -> {
            list.removeAllViews(); String q=filter.getText().toString().toLowerCase(Locale.ROOT);
            for(ResolveInfo ri:apps) {
                String name=ri.loadLabel(getPackageManager()).toString();
                if(name.toLowerCase(Locale.ROOT).contains(q)) add(list,appRow(ri));
            }
        };
        filter.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int before,int count){refresh.run();}
            public void afterTextChanged(android.text.Editable e){}
        });
        refresh.run();
        add(root,button("← Voltar à tela inicial",this::showHome));
    }
    private void showSettings() {
        base("Personalização");
        add(content,label("Aparência",18,true));
        add(content,button(light?"Ativar tema escuro":"Ativar tema claro",()->{
            light=!light; prefs.edit().putBoolean("light",light).apply(); showSettings();
        }));
        add(content,label("Cor de destaque",16,true));
        int[] colors={0xff9b78ff,0xff4caf50,0xff03a9f4,0xffff7043,0xffec407a};
        String[] names={"Roxo","Verde","Azul","Laranja","Rosa"};
        for(int n=0;n<colors.length;n++){ final int c=colors[n]; add(content,button("●  "+names[n],()->{
            accent=c; prefs.edit().putInt("accent",c).apply(); showSettings();
        }));}
        add(content,button("Escolher papel de parede",()->{
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i,42);
        }));
        add(content,button("← Voltar à tela inicial",this::showHome));
    }
    @Override protected void onActivityResult(int req,int res,Intent data) {
        super.onActivityResult(req,res,data);
        if(req==42 && res==RESULT_OK && data!=null && data.getData()!=null) {
            Uri uri=data.getData();
            try {
                getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getSharedPreferences("theme",MODE_PRIVATE).edit().putString("wallpaper",uri.toString()).apply();
                Toast.makeText(this,"Imagem selecionada. O papel de parede do sistema pode ser definido pelo app Configurações.",Toast.LENGTH_LONG).show();
            } catch(Exception e) { Toast.makeText(this,"Imagem selecionada",Toast.LENGTH_SHORT).show(); }
        }
    }
}
