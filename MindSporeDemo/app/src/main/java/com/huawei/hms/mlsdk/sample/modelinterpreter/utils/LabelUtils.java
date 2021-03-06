/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.hms.mlsdk.sample.modelinterpreter.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LabelUtils {
    private static final String TAG = "CustModelActivity";

    private static final int PRINT_LENGTH = 10;

    public static ArrayList<String> readLabels(Context context, String assetFileName) {
        ArrayList<String> result = new ArrayList<>();
        InputStream is = null;
        try {
            is = context.getAssets().open(assetFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String readString;
            while ((readString = br.readLine()) != null) {
                result.add(readString);
            }
            br.close();
        } catch (IOException error) {
            Log.e(TAG, "Asset file doesn't exist: " + error.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException error) {
                    Log.e(TAG, "close failed: " + error.getMessage());
                }
            }
        }
        return result;
    }

    public static String processResult(List<String> labelList, float[] probabilities) {
        Map<String, Float> localResult = new HashMap<>();
        ValueComparator compare = new ValueComparator(localResult);
        for (int i = 0; i < probabilities.length; i++) {
            localResult.put(labelList.get(i), probabilities[i]);
        }
        TreeMap<String, Float> result = new TreeMap<>(compare);
        result.putAll(localResult);

        StringBuilder builder = new StringBuilder();

        int total = 0;
        DecimalFormat df = new DecimalFormat("0.00%");
        for (Map.Entry<String, Float> entry : result.entrySet()) {
            if (total == PRINT_LENGTH || entry.getValue() <= 0) {
                break;
            }

            builder.append("No ")
                    .append(total)
                    .append("：")
                    .append(entry.getKey())
                    .append(" / ")
                    .append(df.format(entry.getValue()))
                    .append(System.lineSeparator());
            total++;
        }

        return builder.toString();
    }

    private static class ValueComparator implements Comparator<String> {
        Map<String, Float> base;

        ValueComparator(Map<String, Float> base) {
            this.base = base;
        }

        @Override
        public int compare(String o1, String o2) {
            if (base.get(o1) >= base.get(o2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
