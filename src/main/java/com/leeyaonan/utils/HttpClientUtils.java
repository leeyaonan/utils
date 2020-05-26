package com.leeyaonan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Apache HttpClient工具类，
 * 用于处理Java版本降级后(11 -> 8)发送Http请求
 * @author Rot
 * @date 2020/5/25 17:10
 */
@Slf4j
public class HttpClientUtils {

    /**
     * 处理get请求
     * @param url 请求路径
     * @param params 参数（Map格式）
     * @return 请求结果
     */
    public static String doGet(String url, Map<String, String> params) {

        // 返回结果
        String result = null;
        // 创建HttpClient对象
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = null;
        try {
            // 拼接参数,可以用URIBuilder,也可以直接拼接在?传值，拼在url后面，如下--httpGet = new
            // HttpGet(uri+"?id=123");
            URIBuilder uriBuilder = new URIBuilder(url);
            if (null != params && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    // 注意：setParameter会覆盖同名参数的值，addParameter则不会
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = uriBuilder.build();
            // 创建get请求
            httpGet = new HttpGet(uri);
            log.info("发送Get请求：" + uri);
            HttpResponse response = httpClient.execute(httpGet);
            // 当状态码为200的时候，返回结果集
            result = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 结果返回
                log.info("请求成功,url={},返回数据={}", uri, result);
            } else {
                log.info("请求失败,url={},返回数据={}", uri, result);
            }
        } catch (Exception e) {
            log.error("请求异常,url={},msg={}", url, e.getMessage());
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != httpGet) {
                httpGet.releaseConnection();
            }
        }
        return result;
    }

    /**
     * 处理post请求（请求体为表单）
     * @param url 请求路径
     * @param params 请求参数(Map)
     * @return 请求结果
     */
    public static String doPost(String url, Map<String, String> params) {
        String result = null;
        // 创建httpclient对象
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            // 参数键值对
            if (null != params && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                NameValuePair pair = null;
                for (String key : params.keySet()) {
                    pair = new BasicNameValuePair(key, params.get(key));
                    pairs.add(pair);
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("请求成功,url={},返回数据={}", url, result);
            } else {
                log.info("请求失败,url={},返回数据={}", url, result);
            }
        } catch (Exception e) {
            log.error("请求异常,url={},msg={}", url, e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != httpPost) {
                // 释放连接
                httpPost.releaseConnection();
            }
        }
        return result;
    }

    /**
     * 处理post请求（请求体为json字符串）
     * @param url
     * @param params
     * @return
     */
    public static String sendJsonStr(String url, String params) {
        String result = null;
        log.info("发送Json请求体,url={},body={}", url, params);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            if (!StringUtils.isEmpty(params)) {
                httpPost.setEntity(new StringEntity(params, StandardCharsets.UTF_8));
            }
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("请求成功,url={},返回数据={}", url, result);
            } else {
                log.info("请求失败,url={},返回数据={}", url, result);
            }
        } catch (IOException e) {
            log.error("请求异常,url={},msg={}", url, e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
