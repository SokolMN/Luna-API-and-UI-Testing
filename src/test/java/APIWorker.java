package HTTP;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;

import java.util.Hashtable;
import java.util.Iterator;


public class APIWorker extends  InitClass {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost requestPOST;
    JSONObject jsonInput;
    HttpResponse response;
    JSONObject jsonOutput;
    String url;
    HttpGet requestGet;

    public void setUrl(String url) {
        this.url = url;
    }

    public void createAccountAPI(){
        try {
            requestPOST = new HttpPost(url);
            jsonInput = new JSONObject();//Создаём JSON и заполняем его нужным для создания пользователя данными
            jsonInput.put("organization_name", orgnizationName );//Название организации из константы класса InitClass
            jsonInput.put("email", loginName); //Имя пользователя из константы класса InitClass
            jsonInput.put("password",password); //Пароль пользователя из константы класса InitClass


            StringEntity params = new StringEntity(jsonInput.toString());
            requestPOST.addHeader("content-type", "application/json");  //Устанавливаем заголовок, в котором указываем, что данные отправляютс через JSON (про этот заголовок можно погуглить и узнать, какие тут вообще бывают значения
            requestPOST.setEntity(params); //Засовываем данные в POST запрос

            response = httpClient.execute(requestPOST); //Выполняем POST запрос и получаем данные

            System.out.println("Отправка POST " + response.getStatusLine() + " "+ requestPOST + "\n" + requestPOST.getEntity());
            System.out.println("Отправка POST " + response.getEntity());

            HttpEntity entity = response.getEntity(); //Этот объект нужен, чтобы вытащить данные из response
            String responseString = EntityUtils.toString(entity, "UTF-8"); //Тут мы преобразовываем все данные из response в строку. Скорее всего она будет в формате JSON
            System.out.println(responseString);

            JSONParser parser = new JSONParser(); //Этот объект нужен, чтобы сконвертировать строку в JSON объект (этого можно было и не делать, но так удобнее потом работать с ответом
            jsonOutput = (JSONObject) parser.parse(responseString);
            tokenAPI = jsonOutput.get("token").toString(); //Как раз то самое удобство. Из документации можно выяснить, что они должны вернуть тег "token". И благодаря тому, что сконвертировали строку в JSON объект, мы можем получать данные таким способом (иначе пришлось бы парсить строку)

            System.out.println("Это ТОКЕН, полученный через API " + jsonOutput.get("token"));

        } catch (Exception e) {
            System.out.println("Ошибка в отправке POST-Запроса " + e);
        }

    }

    public void createTokenForCurrentAccountAPI(){
        try {
            jsonInput.clear();
            jsonOutput.clear();
            jsonInput.put("token_data", "somedata");


            String encodeLoginPassword = encodeLoginPassword();
            StringEntity params = new StringEntity(jsonInput.toString());


            requestPOST.reset();
            requestPOST = new HttpPost(url);
            requestPOST.addHeader("content-type", "application/json");
            //requestPOST.addHeader("Authorization","Basic U09LT1ZAbWFpbDQxLnJ1OjA1MDQ5MzA1MDQ5Mw==");
            requestPOST.addHeader("Authorization","Basic " + encodeLoginPassword);
            requestPOST.setEntity(params);

            HeaderIterator it = requestPOST.headerIterator();

            while(it.hasNext()){
                System.out.println(it.next());
            }

            System.out.println("Отправляем запрос на создание нового токена");
            response = httpClient.execute(requestPOST);
            HttpEntity entity = response.getEntity(); //Этот объект нужен, чтобы вытащить данные из response
            String responseString = EntityUtils.toString(entity, "UTF-8"); //Тут мы преобразовываем все данные из response в строку. Скорее всего она будет в формате JSON
            System.out.println(responseString);

            JSONParser parser = new JSONParser(); //Этот объект нужен, чтобы сконвертировать строку в JSON объект (этого можно было и не делать, но так удобнее потом работать с ответом
            jsonOutput = (JSONObject) parser.parse(responseString);

        } catch (Exception e) {
            System.out.println("Ошибка в отправке POST-Запроса " + e);
        }
    }


    public Hashtable getTokensOwnedByCurrentAccountAPI(){
        clearAllInstances();
        String encodeLoginPassword = encodeLoginPassword();
        requestGet = new HttpGet(url);
        System.out.println("Добавляем заголовок с ковенртированными логином и паролем");
        requestGet.addHeader("Authorization", "Basic " + encodeLoginPassword);
        System.out.println("Добавили заголовок с ковенртированными логином и паролем");

        try {
            System.out.println("Отправляем запрос GET");
            response = httpClient.execute(requestGet);
            System.out.println("Отправили запрос GET");
            HttpEntity entity = response.getEntity(); //Этот объект нужен, чтобы вытащить данные из response
            String responseString = EntityUtils.toString(entity, "UTF-8"); //Тут мы преобразовываем все данные из response в строку. Скорее всего она будет в формате JSON
            System.out.println("Данные из GET-запроса " + responseString);


            System.out.println("Начинаем парсить данные из Get запроса");
            JSONParser parser = new JSONParser();
            jsonOutput = (JSONObject) parser.parse(responseString);
            JSONArray jsonArray = (JSONArray) jsonOutput.get("tokens");
            System.out.println("После парсинга: " + jsonArray.toJSONString());
            System.out.println("Первый токен " + jsonArray.get(1).toString());
            jsonOutput = (JSONObject) jsonArray.get(1);
            System.out.println("Проверочка " + jsonOutput.toString());
            jsonOutput.get("id");
            System.out.println("Id токена " + jsonOutput.get("id"));

            System.out.println("Начинаем собирать список токенов");

            Hashtable tokenTable = new Hashtable(); // здесь хранятся токены ключ - значение
            Iterator tokenIterator = jsonArray.iterator();
            while(tokenIterator.hasNext()){
                jsonOutput = (JSONObject) tokenIterator.next();
                tokenTable.put(jsonOutput.get("id"), jsonOutput.get("token_data"));
            }

            System.out.println(tokenTable.size());
            return  tokenTable;

        } catch (Exception e) {
            System.out.println("Ошибка в GET-запросе " + e);
            return null;
        }

    }

    private String encodeLoginPassword(){ //Метод для конвертации в формат Base64

        //Переводим логин и пароль в Base64, так как для LUNA нужно передавать данные для текущего аккаунта в таком виде
        byte[] encodedBytes = Base64.encodeBase64((loginName +":" + password).getBytes());
        String encodeLoginPassword = new String(encodedBytes);
        System.out.println("Это данные в формате BASE64 " + new String(encodedBytes));

        return encodeLoginPassword;
    }

    private void clearAllInstances(){
        this.jsonOutput.clear();
        this.jsonInput.clear();

    }

    public String getToken(){
        return jsonOutput.get("token").toString();
    }
}
