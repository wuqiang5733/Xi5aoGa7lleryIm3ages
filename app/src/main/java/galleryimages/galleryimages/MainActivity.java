package galleryimages.galleryimages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    boolean boolean_folder;
    Adapter_PhotosFolder obj_adapter;
    GridView gv_folder;
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv_folder = (GridView)findViewById(R.id.gv_folder);

        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });


        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            fetchMediaFolder();

            fn_imagespath();
        }


    }

    public ArrayList<Model_images> fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data,
                column_index_folder_name,
                column_index_date_taken,
                column_index_description,
                column_index_displayName,
                colunm_index_mimeType;


        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        column_index_date_taken = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
        column_index_description = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
        column_index_displayName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        colunm_index_mimeType = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);


        ArrayList<String> latest = new ArrayList<>();


        int lastestIndex = 0 ;
        Model_images latest_model = new Model_images();
        latest_model.setStr_folder("Latest");  // 创建一个名为"Latest"的文件夹
        al_images.add(latest_model);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);  // 路径
            Log.d("path", absolutePathOfImage);
            Log.d("bucket", cursor.getString(column_index_folder_name));
            Log.d("date", cursor.getString(column_index_date_taken));
            Log.d("name", cursor.getString(column_index_displayName));
            Log.d("type", cursor.getString(colunm_index_mimeType));
//            Log.d("des", cursor.getString(column_index_description));
            if (lastestIndex < 5){
                latest.add(absolutePathOfImage); // 完整路径
            }
            lastestIndex++;


            for (int i = 0; i < al_images.size(); i++) {
                Log.d("size", String.valueOf(al_images.size()));
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) { // 如果是文件夹
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath()); // 把原来的都读取出来
                al_path.add(absolutePathOfImage); // （ 完整路径 ） 把新的这个也加上
                al_images.get(int_position).setAl_imagepath(al_path);  // 全部赋予

            } else {  // 如果不是文件夹，那就再创建一个新的文件夹
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage); // 完整路径
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));  // 创建一个文件夹
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }

        latest_model.setAl_imagepath(latest);// 把最新的图片 .....
        latest = null;
//        lastestIndex = null;

        for (int i = 0; i < al_images.size(); i++) {
            Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
            }
        }
        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }
    public void fetchMediaFolder(){

        ArrayList<MediaFolderModel> mediaFolderModels = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        String tempMediaFolderName = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

//        String orderBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
//        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        cursor.moveToNext();
        // 下面这两句一定要在 cursor.moveToNext();  之后执行
        tempMediaFolderName = cursor.getString(column_index_folder_name); // 文件夹
        absolutePathOfImage = cursor.getString(column_index_data);  // 路径

        mediaFolderModels.add(new MediaFolderModel(tempMediaFolderName,25,absolutePathOfImage));
        cursor.close();
//        projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        // 第二次查询
        orderBy = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContentResolver().query(uri, projection, null, null, orderBy);
//        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        cursor.moveToNext();

        tempMediaFolderName = cursor.getString(column_index_folder_name); // 文件夹
        absolutePathOfImage = cursor.getString(column_index_data);  // 路径
        MediaFolderModel tempMediaFolderModel = new MediaFolderModel(tempMediaFolderName,1,absolutePathOfImage);
        mediaFolderModels.add(tempMediaFolderModel);


        int folderItemIndex = 0;
        while (cursor.moveToNext()) {
            String temp = cursor.getString(column_index_folder_name);
            if (tempMediaFolderName.equals(temp)){ // 如果还是上一个文件夹
                folderItemIndex++;
                tempMediaFolderModel.setMediaItemSum(folderItemIndex+1);
            }else{  // 如果不是原来的文件夹了
//                mediaFolderModels.add(new MediaFolderModel(tempMediaFolderName,folderItemIndex+1,absolutePathOfImage));
                tempMediaFolderName = cursor.getString(column_index_folder_name); // 文件夹
                absolutePathOfImage = cursor.getString(column_index_data);  // 路径
                tempMediaFolderModel = new MediaFolderModel(tempMediaFolderName,1,absolutePathOfImage);
                mediaFolderModels.add(tempMediaFolderModel);

                folderItemIndex = 0;  // 重新设置元素索引
            }

        }
        Log.d("WQWQ",String.valueOf(mediaFolderModels.size()));
        Log.d("WQWQ","+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

        for(MediaFolderModel mediaFolderModel:mediaFolderModels){
            Log.d("WQWQ",mediaFolderModel.getFolderName());
            Log.d("WQWQ",String.valueOf(mediaFolderModel.getMediaItemSum()));
            Log.d("WQWQ",mediaFolderModel.getFirstItemImage());
            Log.d("WQWQ","+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        }
cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        fn_imagespath();
                    } else {
                        Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

}
