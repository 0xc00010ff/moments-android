package com.tikkunolam.momentsintime;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDealer {
    /**
     * Class for dealing with files and Uris
     */

    final static String TAG = "FileDealer";


    public FileDealer() {

    }


    public String getPath(final Context context, final Uri uri) {
        // takes a Uri and returns the path to the file, whatever/wherever that file may be

        // determines if the API Level is >= 19
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                }

            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            }

            // MediaProvider
            else if (isMediaDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                }

                else if ("video".equals(type)) {

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                }

                else if ("audio".equals(type)) {

                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);

            }

        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        // get the data column from the ContentProvider associated with the Uri

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {

            // get a cursor for the data column of the ContentProvider
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);

            }

        }

        finally {

            if (cursor != null) cursor.close();

        }

        return null;

    }

    public boolean isExternalStorageDocument(Uri uri) {
        // checks the authority to determine if it's in external storage

        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }

    public boolean isDownloadsDocument(Uri uri) {
        // checks the authority to determine if it's in downloads

        return "com.android.providers.downloads.documents".equals(uri.getAuthority());

    }

    public boolean isMediaDocument(Uri uri) {
        // checks the authority to determine if it's in Media

        return "com.android.providers.media.documents".equals(uri.getAuthority());

    }


    public byte[] fullyReadFileToBytes(File file) throws IOException {
        // takes a file and reads it into a byte array

        // get the size of the file
        int size = (int) file.length();

        // make a byte array to house the file
        byte bytes[] = new byte[size];

        // a buffer to read into
        byte tmpBuff[] = new byte[size];

        FileInputStream fileInputStream= new FileInputStream(file);

        try {

            int read = fileInputStream.read(bytes, 0, size);

            if (read < size) {

                int remain = size - read;

                while (remain > 0) {

                    read = fileInputStream.read(tmpBuff, 0, remain);

                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);

                    remain -= read;

                }

            }

        }
        catch (IOException e) {

            throw e;

        }
        finally {

            fileInputStream.close();

        }

        return bytes;

    }

}
