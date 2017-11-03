package io.github.alexlondon07.demodevices;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView main_iv_gallery, main_iv_camera;
    private RecyclerView main_recycler_viewItems;
    private LinearLayout main_linearLayoutPhotoItems;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> arrayFiles;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadView();
        setListener();
        arrayFiles = new ArrayList<>();
        callAdapter();
        observerLayoutGetSize();
    }

    private void observerLayoutGetSize() {
        ViewTreeObserver observer = main_linearLayoutPhotoItems.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                main_linearLayoutPhotoItems.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                photoAdapter.setSize(main_linearLayoutPhotoItems.getWidth(), main_linearLayoutPhotoItems.getHeight());
                photoAdapter.setFiles(arrayFiles);
                photoAdapter.notifyDataSetChanged();
            }
        });

    }

    private void callAdapter() {
        photoAdapter = new PhotoAdapter(MainActivity.this);
        photoAdapter.setFiles(arrayFiles);
        main_recycler_viewItems.setAdapter(photoAdapter);
    }

    private void setListener() {

        main_iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        main_iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
    }

    private void showGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if(Build.VERSION.SDK_INT < Constants.GALLERY_KIT_KAT){
            startActivityForResult(intent, Constants.GALLERY_KIT_KAT);
        }else {
            String[] type = {"image/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, type);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, Constants.GALLERY);
        }
    }

    private void showCameraIntent() {

        Intent takePictureIntent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = null;

        if(takePictureIntent.resolveActivity(getPackageManager()) !=null){
            try {
                photoFile = createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if (photoFile !=null) {

            Uri photoUri = FileProvider.getUriForFile(this, "io.github.alexlondon07.demodevices", photoFile);
            List<ResolveInfo> resolveInfoList = getPackageManager().
                    queryIntentActivities(takePictureIntent, getPackageManager().MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo: resolveInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            super.startActivityForResult(takePictureIntent, Constants.CAMERA_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {

        String imageFileName = Constants.PREFIX_FILE_IMAGE + new SimpleDateFormat(Constants.FORMAT_DATE_FILE).format(new Date());
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null && !storageDir.exists()) {
            boolean result = storageDir.mkdir();
            if (!result) {
                return null;
            }
        }
        return File.createTempFile(imageFileName, Constants.SUFFIX_FILE_IMAGE, storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.GALLERY_KIT_KAT){
            resultGalleryKitKatLess(data.getData());
        }

        if(requestCode == Constants.GALLERY){
            resultGalleryKitKatHigher(data);
        }

        if(requestCode == Constants.CAMERA_CAPTURE){
            resultCameraCapture();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void resultCameraCapture(){
        if (photoFile != null) {
            setArrayFileName(photoFile.getPath());
        }
    }

    private void resultGalleryKitKatHigher(Intent data) {
        ClipData clipData = data.getClipData();

        if(clipData == null){
            resultGalleryKitKatLess(data.getData());
        }else {
            for (int i = 0; i < clipData.getItemCount(); i++){
                grantUriPermission(getPackageName(), clipData.getItemAt(i).getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                setArrayFileName(clipData.getItemAt(i).getUri().toString());
            }
        }
    }


    private void resultGalleryKitKatLess(Uri uri) {
        grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        setArrayFileName(uri.toString());
    }

    private void setArrayFileName(String file) {
        arrayFiles.add(file);
        photoAdapter.setFiles(arrayFiles);
        photoAdapter.notifyDataSetChanged();
    }


    private void showGallery() {
        if(Permissions.isGrantedPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            showGalleryIntent();
        }else{
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            Permissions.verifyPermissions(this, permissions);
        }
    }

    private void showCamera() {
        if(Permissions.isGrantedPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            showCameraIntent();
        }else{
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            Permissions.verifyPermissions(this, permissions);
        }
    }

    private void loadView() {
        main_iv_gallery = (ImageView) findViewById(R.id.main_iv_gallery);
        main_iv_camera = (ImageView) findViewById(R.id.main_iv_camera);
        main_recycler_viewItems = (RecyclerView) findViewById(R.id.main_recyclerViewItems);
        main_linearLayoutPhotoItems = (LinearLayout) findViewById(R.id.main_layout_item);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        main_recycler_viewItems.setLayoutManager(layoutManager);
    }
}
