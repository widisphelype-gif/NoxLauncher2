package br.com.launchernox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root, content;
    private int accent = 0xff48a8ff;
    private boolean light = false;
    private SharedPreferences prefs;
    private final List<ResolveInfo> apps = new ArrayList<>();
    private EditText homeSearch;
    private String savedWallpaper = null;

    private int fg() { return light ? 0xff172033 : Color.WHITE; }
    private int muted() { return light ? 0xff596579 : 0xffc1cde0; }
    private int panel() { return light ? 0xeef7faff : 0xdd101d32; }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String s, int size, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s); v.setTextSize(size); v.setTextColor(fg());
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        v.setGravity(Gravity.CENTER_VERTICAL); v.setPadding(8, 5, 8, 5);
        return v;
    }
    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private LinearLayout col() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }
    private void gap(LinearLayout parent, int h) {
        View v = new View(this); parent.addView(v, new LinearLayout.LayoutParams(1,h));
    }
    private TextView action(String s, Runnable r) {
        TextView v = text(s, 15, true); v.setGravity(Gravity.CENTER);
        v.setBackground(shape(0xdd253650, 24)); v.setPadding(15,12,15,12);
        v.setOnClickListener(w -> r.run()); return v;
    }
    private void add(LinearLayout parent, View v, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = bottom; parent.addView(v,p);
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(0xff071426);
        getWindow().setNavigationBarColor(0xff0b1729);
        prefs = getSharedPreferences("theme", MODE_PRIVATE);
        light = prefs.getBoolean("light", false);
        accent = prefs.getInt("accent", 0xff48a8ff);
        savedWallpaper = prefs.getString("wallpaper", null);
        loadApps();
        showHome();
    }
    private void base(String title) {
        root = col();
        GradientDrawable wallpaper = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                light ? new int[]{0xffe6f4ff,0xffa7d5f7,0xffdceaff} :
                new int[]{0xff071426,0xff123e72,0xff071426});
        root.setBackground(wallpaper);
        setContentView(root);
        LinearLayout header = row(); header.setPadding(20,10,20,4);
        TextView brand = text("◈  NOX", 18, true); brand.setTextColor(accent);
        header.addView(brand, new LinearLayout.LayoutParams(0,48,1));
        TextView settings = text("⚙",24,true); settings.setGravity(Gravity.CENTER);
        settings.setOnClickListener(v -> showSettings());
        header.addView(settings, new LinearLayout.LayoutParams(48,48));
        root.addView(header);
        content = col(); content.setPadding(22,8,22,14);
        ScrollView sc = new ScrollView(this); sc.setFillViewport(false); sc.addView(content);
        root.addView(sc, new LinearLayout.LayoutParams(-1,0,1));
        buildTaskbar();
    }
    private void buildTaskbar() {
        LinearLayout bar = row(); bar.setGravity(Gravity.CENTER); bar.setPadding(10,8,10,8);
        bar.setBackground(shape(0xe617263b, 30));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,60);
        bp.setMargins(12,0,12,10); root.addView(bar,bp);
        TextView start = text("⊞",28,true); start.setGravity(Gravity.CENTER);
        start.setTextColor(accent); bar.addView(start,new LinearLayout.LayoutParams(48,-1));
        start.setOnClickListener(v -> showDrawer(""));
        TextView searchBtn = text("⌕",24,true); searchBtn.setGravity(Gravity.CENTER);
        bar.addView(searchBtn,new LinearLayout.LayoutParams(44,-1));
        searchBtn.setOnClickListener(v -> showDrawer(""));
        TextView all = text("▦",21,true); all.setGravity(Gravity.CENTER);
        bar.addView(all,new LinearLayout.LayoutParams(44,-1));
        all.setOnClickListener(v -> showDrawer(""));
        TextView clock = text(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),13,true);
        clock.setGravity(Gravity.CENTER); bar.addView(clock,new LinearLayout.LayoutParams(0,-1,1));
    }
    private void showHome() {
        base("Home");
        TextView clock = text(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),56,true);
        clock.setGravity(Gravity.CENTER); clock.setShadowLayer(18,0,0,0xff398bff);
        add(content,clock,0);
        TextView date = text(new SimpleDateFormat("EEEE, d 'de' MMMM",new Locale("pt","BR")).format(new Date()),15,false);
        date.setTextColor(muted()); date.setGravity(Gravity.CENTER); add(content,date,22);
        homeSearch = new EditText(this); homeSearch.setSingleLine(true); homeSearch.setTextColor(fg());
        homeSearch.setHintTextColor(muted()); homeSearch.setHint("⌕   Pesquisar aplicativos");
        homeSearch.setTextSize(15); homeSearch.setPadding(18,10,18,10);
        homeSearch.setBackground(shape(0xdd243650,28));
        add(content,homeSearch,14);
        add(content,action("⌕   Pesquisar",()->showDrawer(homeSearch.getText().toString())),10);
        TextView section = text("Fixados",19,true); section.setPadding(4,12,4,10); add(content,section,4);
        LinearLayout tiles = row(); tiles.setGravity(Gravity.CENTER); content.addView(tiles);
        String[] tileNames = {"Arquivos","Navegador","Configurações","Todos os apps"};
        String[] glyphs = {"▤","◉","⚙","▦"};
        for(int i=0;i<tileNames.length;i++) {
            final int ix=i; LinearLayout tile=col(); tile.setGravity(Gravity.CENTER);
            tile.setPadding(4,10,4,10); tile.setBackground(shape(0x662e4a6b,20));
            LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,104,1); tp.setMargins(3,2,3,2); tiles.addView(tile,tp);
            TextView g=text(glyphs[i],25,true); g.setGravity(Gravity.CENTER); g.setTextColor(accent); tile.addView(g);
            TextView n=text(tileNames[i],10,true); n.setGravity(Gravity.CENTER); n.setMaxLines(2); tile.addView(n);
            tile.setOnClickListener(v->{ if(ix==2)showSettings(); else if(ix==3)showDrawer(""); else if(ix==0)openPackage("com.google.android.documentsui"); else openPackage("com.android.chrome"); });
        }
        TextView rec = text("Recomendados",19,true); rec.setPadding(4,18,4,10); add(content,rec,4);
        int shown=0;
        for(ResolveInfo ri:apps) { if(shown++>=5) break; add(content,appRow(ri),7); }
        TextView note=text("Interface inspirada no Windows 11 • Android",11,false);
        note.setTextColor(muted()); note.setGravity(Gravity.CENTER); add(content,note,8);
    }
    private void loadApps() {
        apps.clear();
        Intent i=new Intent(Intent.ACTION_MAIN,null); i.addCategory(Intent.CATEGORY_LAUNCHER);
        apps.addAll(getPackageManager().queryIntentActivities(i,0));
        Collections.sort(apps, Comparator.comparing(a->a.loadLabel(getPackageManager()).toString().toLowerCase(Locale.ROOT)));
    }
    private View appRow(ResolveInfo ri) {
        LinearLayout r=row(); r.setPadding(12,5,12,5); r.setBackground(shape(0x77253650,18));
        Drawable d=ri.loadIcon(getPackageManager());
        TextView icon=text("",16,false); icon.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
        r.addView(icon,new LinearLayout.LayoutParams(48,48));
        TextView name=text(ri.loadLabel(getPackageManager()).toString(),15,true);
        r.addView(name,new LinearLayout.LayoutParams(0,48,1));
        TextView arrow=text("›",25,false); arrow.setTextColor(muted()); r.addView(arrow);
        r.setOnClickListener(v->{try { Intent launch=getPackageManager().getLaunchIntentForPackage(ri.activityInfo.packageName); if(launch!=null)startActivity(launch); } catch(Exception e){Toast.makeText(this,"Não foi possível abrir o app",Toast.LENGTH_SHORT).show();}});
        return r;
    }
    private void showDrawer(String query) {
        base("Iniciar");
        TextView heading=text("Seus aplicativos",23,true); add(content,heading,8);
        EditText filter=new EditText(this); filter.setSingleLine(true); filter.setTextColor(fg());
        filter.setHintTextColor(muted()); filter.setHint("Pesquisar pelo nome"); filter.setText(query);
        filter.setPadding(16,8,16,8); filter.setBackground(shape(0xdd243650,24)); add(content,filter,14);
        LinearLayout list=col(); content.addView(list);
        Runnable refresh=()->{list.removeAllViews();String q=filter.getText().toString().toLowerCase(Locale.ROOT);
            for(ResolveInfo ri:apps) if(ri.loadLabel(getPackageManager()).toString().toLowerCase(Locale.ROOT).contains(q)) add(list,appRow(ri),7);};
        filter.addTextChangedListener(new android.text.TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int before,int count){refresh.run();}
            public void afterTextChanged(android.text.Editable e){}
        });
        refresh.run(); add(root,action("←  Voltar à tela inicial",this::showHome),8);
    }
    private void showSettings() {
        base("Personalização");
        add(content,text("Aparência",20,true),8);
        add(content,action(light?"Ativar tema escuro":"Ativar tema claro",()->{light=!light;prefs.edit().putBoolean("light",light).apply();showSettings();}),10);
        add(content,text("Cor de destaque",16,true),5);
        int[] colors={0xff48a8ff,0xff9b78ff,0xff4caf50,0xffff7043,0xffec407a};
        String[] names={"Azul Windows","Roxo","Verde","Laranja","Rosa"};
        for(int n=0;n<colors.length;n++){final int c=colors[n];add(content,action("●  "+names[n],()->{accent=c;prefs.edit().putInt("accent",c).apply();showSettings();}),7);}
        add(content,action("Escolher imagem de papel de parede",()->{
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,42);
        }),10);
        add(content,action("←  Voltar à tela inicial",this::showHome),8);
    }
    private void openPackage(String pkg) {
        try { Intent i=getPackageManager().getLaunchIntentForPackage(pkg); if(i!=null)startActivity(i); else showDrawer(""); }
        catch(Exception e){showDrawer("");}
    }
    @Override protected void onActivityResult(int req,int res,Intent data) {
        super.onActivityResult(req,res,data);
        if(req==42&&res==RESULT_OK&&data!=null&&data.getData()!=null) {
            Uri uri=data.getData();
            try { getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                prefs.edit().putString("wallpaper",uri.toString()).apply();
                Toast.makeText(this,"Imagem salva nas preferências. O fundo será aplicado numa atualização futura.",Toast.LENGTH_LONG).show();
            } catch(Exception e){Toast.makeText(this,"Imagem selecionada",Toast.LENGTH_SHORT).show();}
        }
    }
}
