package com.vaavud.util;


/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.vaavud.android.VaavudApplication;
import com.vaavud.android.model.entity.Device;
import com.vaavud.android.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* 
 * This class just handles getting the client since we don't need to have more than
 * one per application
 */
public class S3Util {
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;

    public static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context,
                    Constants.AWS_ACCOUNT_ID,
                    Constants.COGNITO_POOL_ID,
                    Constants.COGNITO_ROLE_UNAUTH,
                    null,
                    Regions.EU_WEST_1);
        }
        return sCredProvider;
    }

    public static String getPrefix(Context context) {
    	if (User.getInstance(context.getApplicationContext()).getEmail()!=null)
    		return "SleipnirSound/"+User.getInstance(context.getApplicationContext()).getEmail()+"/"+Device.getInstance(context.getApplicationContext()).getModel()+"/";
        return  "SleipnirSound/noUser/"+Device.getInstance(context.getApplicationContext()).getModel()+"/";
    }

    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context));
        }
        return sS3Client;
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static boolean doesBucketExist() {
        return sS3Client.doesBucketExist(Constants.BUCKET_NAME.toLowerCase(Locale.US));
    }

    public static void createBucket() {
        sS3Client.createBucket(Constants.BUCKET_NAME.toLowerCase(Locale.US));
    }

    public static void deleteBucket() {
        String name = Constants.BUCKET_NAME.toLowerCase(Locale.US);
        List<S3ObjectSummary> objData = sS3Client.listObjects(name).getObjectSummaries();
        if (objData.size() > 0) {
            DeleteObjectsRequest emptyBucket = new DeleteObjectsRequest(name);
            List<KeyVersion> keyList = new ArrayList<KeyVersion>();
            for (S3ObjectSummary summary : objData) {
                keyList.add(new KeyVersion(summary.getKey()));
            }
            emptyBucket.withKeys(keyList);
            sS3Client.deleteObjects(emptyBucket);
        }
        sS3Client.deleteBucket(name);
    }
}
