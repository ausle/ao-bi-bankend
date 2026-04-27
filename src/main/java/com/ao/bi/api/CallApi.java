package com.ao.bi.api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.squareup.okhttp.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallApi {

    private static final String API_KEY = "sk-api-Iv-0IFMWdThB3DMv5pZ39L3HeVhDi0_k3EJqhSTTGIvSxH8n5_c-xTKrrpNBl77HmqnL2It0g40oONokekeFUa_jd7X_QoLK9lVCFiSKakkyFQr23mB1-Co";
    /*
        参考文档：https://platform.minimaxi.com/document/%E5%AF%B9%E8%AF%9D?key=36954880539f4e2ab8b13067
     */
    private static final String BASE_URL = "https://api.minimax.chat/v1/text/chatcompletion_v2";

    public static final String AOBI_CHAR = "AOBI";
    public static void main(String[] args) {
        callApi("分析网站用户的增长情况","柱状图","日期,用户数 1号,10  2号,20 3号,30");
    }

    public static String callApi(String howToAnalyze, String chartType,String csvData) {
        // 构造请求消息
        List<Map<String, String>> messages = new ArrayList<>();

        String prompt = "分析需求：%s\n" +
                "原始数据：%s\n" +
                "请根据这两部分内容，按照以下指定格式生成内容(数据分为两部分，第一部分是js代码，第二部分是分析结论，两部分之间用英文"+AOBI_CHAR+"分隔，不要输出任何多余内容)：\n" +
                "{前端的Echarts的option配置对象的javascript代码,只需要代码,代码的图表类型需要是：%s}\n" +
                "{明确的数据分析结论,越详细越好,不要有任何解释或注释}";
        prompt=String.format(prompt,howToAnalyze,csvData,chartType);

        Map<String, String> msg = new HashMap<>();
        //Minimax（零一万物）的 chatcompletion_v2 接口，不支持 system 角色
        msg.put("role", "user");
        msg.put("content", prompt);
        msg.put("name","用户");
        messages.add(msg);

        // 设置系统提示词，设定AI将要扮演的角色
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, String>> botSettingList = new ArrayList<>();
        Map<String, String> botConfig = new HashMap<>();
        botConfig.put("bot_name", "智能数据分析助手");
        botConfig.put("content", "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：{数据分析的需求或目标}\n" +
                "原始数据：{csv格式的原始数据，用英文逗号分隔}");
        botSettingList.add(botConfig);
        parameters.put("bot_setting", botSettingList);

        // 构造请求体
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "MiniMax-Text-01");
        requestBodyMap.put("parameters", parameters);
        requestBodyMap.put("messages", messages);

        // 请求体转json字符串
        String jsonBody = JSONUtil.toJsonStr(requestBodyMap);

        // 拼接 URL（必须带 GroupId）
        String url = BASE_URL;

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonBody))
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        try {
            // 发送请求
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                System.out.println("===== 调用成功 =====");
                System.out.println(result);

                // ============ 核心解析开始 ============
                JSONObject root = JSONUtil.parseObj(result);

                // 1. 获取 base_resp（判断是否成功）
                JSONObject baseResp = root.getJSONObject("base_resp");
                int statusCode = baseResp.getInt("status_code");
                if (statusCode==0){
                    // 2. 获取 AI 回答内容
                    JSONArray choices = root.getJSONArray("choices");
                    JSONObject firstChoice = choices.getJSONObject(0); // 取第一个结果
                    JSONObject message = firstChoice.getJSONObject("message");

                    String content = message.getStr("content"); // AI 回答文本
                    return content;
                }
            } else {
                System.err.println("调用失败，状态码：" + response.code());
                System.err.println("错误信息：" + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
