package com.example.hotfix.utils.loadPlugApkUtils.manifestparcer;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.PatternMatcher;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class IntentFilterParser {

    String apkPath;

    public IntentFilterParser(String apkPath) {
        this.apkPath = apkPath;
    }

    public Map<String, IntentFilter> getIntentFilter() {
        Map<String, IntentFilter> intentFilters = new HashMap<String, IntentFilter>();
        AssetManager assetManager = null;
        XmlResourceParser parser = null;
        try {
            assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.invoke(assetManager, apkPath);
            parser = assetManager.openXmlResourceParser(ANDROID_MANIFEST_FILENAME);
            parseManifest(parser, intentFilters);
            parser.close();
            assetManager.close();
            parser = null;
            assetManager = null;
        } catch (Exception e) {

        } finally {
            if (parser != null) {
                parser.close();
                parser = null;
            }
            if (assetManager != null) {
                assetManager.close();
                assetManager = null;
            }
        }
        return intentFilters;
    }

    private void parseManifest(XmlResourceParser parser, Map<String, IntentFilter> intentFilters) throws Exception {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName == null) {
                continue;
            }

            // TODO 获取所有的activity、receiver和service
            if (tagName.equals("activity") || tagName.equals("receiver") || tagName.equals("service")) {
                String activityName = parser.getAttributeValue(ANDROID_RESOURCES, "name");
                if (activityName != null) {
                    parseActivity(activityName, intentFilters, parser);
                }
            }
        }
    }

    private void parseActivity(String activityName, Map<String, IntentFilter> intentFilters, XmlResourceParser parser) throws Exception {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName == null) {
                continue;
            }

            if (tagName.equals("intent-filter")) {
                IntentFilter mFilter = new IntentFilter();
                parseIntentFilter(mFilter, parser);
                intentFilters.put(activityName, mFilter);
            }
        }
    }

    /**
     * 解析Intent-Filter字段
     *
     * @param mFilter
     * @param attrs
     * @throws Exception
     */
    @SuppressLint("NewApi")
    private void parseIntentFilter(IntentFilter mFilter, XmlResourceParser attrs) throws Exception {
        int outerDepth = attrs.getDepth();
        int type;
        while ((type = attrs.next()) != XmlPullParser.END_DOCUMENT && (type != XmlPullParser.END_TAG || attrs.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }
            String nodeName = attrs.getName();
            if (nodeName == null) {
                continue;
            }

            if (nodeName.equals("action")) {
                String value = attrs.getAttributeValue(ANDROID_RESOURCES, "name");
                if (!TextUtils.isEmpty(value)) {
                    mFilter.addAction(value);
                }
            } else if (nodeName.equals("category")) {
                String value = attrs.getAttributeValue(ANDROID_RESOURCES, "name");
                if (!TextUtils.isEmpty(value)) {
                    mFilter.addCategory(value);
                }
            } else if (nodeName.equals("data")) {
                /**
                 * Specify a URI authority host that is handled, as per 
                 * IntentFilter.addDataAuthority() . [string] 
                 */
                String host = attrs.getAttributeValue(ANDROID_RESOURCES, "host");
                /**
                 * Specify a URI authority port that is handled, as per 
                 * IntentFilter.addDataAuthority() . [string] 
                 */

                String port = attrs.getAttributeValue(ANDROID_RESOURCES, "port");
                if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)) {
                    mFilter.addDataAuthority(host, port);
                }

                /**
                 * Specify a URI scheme that is handled, as per 
                 * IntentFilter.addDataScheme() . [string] 
                 */
                String scheme = attrs.getAttributeValue(ANDROID_RESOURCES, "scheme");
                if (!TextUtils.isEmpty(scheme)) {
                    mFilter.addDataScheme(scheme);
                }

                /**
                 * Specify a URI path that must exactly match, as per 
                 * IntentFilter.addDataPath() with PATTERN_LITERAL. [string] 
                 */
                String path = attrs.getAttributeValue(ANDROID_RESOURCES, "path");
                if (!TextUtils.isEmpty(path)) {
                    mFilter.addDataPath(path, PatternMatcher.PATTERN_LITERAL);
                }
                /**
                 * Specify a URI path that must be a prefix to match, as per 
                 * IntentFilter.addDataPath() with PATTERN_PREFIX. [string] 
                 */
                String pathPrefix = attrs.getAttributeValue(ANDROID_RESOURCES, "pathPrefix");
                if (!TextUtils.isEmpty(pathPrefix)) {
                    mFilter.addDataPath(pathPrefix, PatternMatcher.PATTERN_PREFIX);
                }

                /**
                 * Specify a URI path that matches a simple pattern, as per 
                 * IntentFilter.addDataPath() with PATTERN_SIMPLE_GLOB. [string] 
                 */
                String pathPattern = attrs.getAttributeValue(ANDROID_RESOURCES, "pathPattern");
                if (!TextUtils.isEmpty(pathPattern)) {
                    mFilter.addDataPath(pathPattern, PatternMatcher.PATTERN_SIMPLE_GLOB);
                }

                /**
                 * Specify a MIME type that is handled, as per 
                 * IntentFilter.addDataType() . [string] 
                 */
                String mimeType = attrs.getAttributeValue(ANDROID_RESOURCES, "mimeType");
                if (!TextUtils.isEmpty(mimeType)) {
                    mFilter.addDataType(mimeType);
                }

                if (android.os.Build.VERSION.SDK_INT >= 19) {
                    /**
                     * Specify a URI scheme specific part that must exactly 
                     * match, as per IntentFilter.addDataSchemeSpecificPart() 
                     * with PATTERN_LITERAL. [string] 
                     */
                    String ssp = attrs.getAttributeValue(ANDROID_RESOURCES, "ssp");
                    mFilter.addDataSchemeSpecificPart(ssp, PatternMatcher.PATTERN_LITERAL);

                    /**
                     * Specify a URI scheme specific part that must be a prefix 
                     * to match, as per IntentFilter.addDataSchemeSpecificPart() 
                     * with PATTERN_PREFIX. [string] 
                     */
                    String sspPrefix = attrs.getAttributeValue(ANDROID_RESOURCES, "sspPrefix");
                    mFilter.addDataSchemeSpecificPart(sspPrefix, PatternMatcher.PATTERN_PREFIX);

                    /**
                     * Specify a URI scheme specific part that matches a simple 
                     * pattern, as per IntentFilter.addDataSchemeSpecificPart() 
                     * with PATTERN_SIMPLE_GLOB. [string] 
                     */
                    String sspPattern = attrs.getAttributeValue(ANDROID_RESOURCES, "sspPattern");
                    mFilter.addDataSchemeSpecificPart(sspPattern, PatternMatcher.PATTERN_SIMPLE_GLOB);
                }
            }
        }
    }

    private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";

}  