package HTTP;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import org.json.simple.parser.JSONParser;

import static org.testng.Assert.assertEquals;

public class APITest extends InitClass{
        /*Сам тест.
        В аннотации BeforeClass сначала идет запрос через API. То есть мы создаем пользователя в системе LUNA (та самая внешняя система,с которой интегрируемся),
        в которой хранятся фотографии клиентов. После того, как создали пользователя через API, необходимо проверить корректно ли сохранились данные в интерфейсе.
        В аннотации Test идет сам тест, в котором мы перемещаемся по страничкам и в конце мы проверяем совпадает ли токен, который мы получили через API с токеном,
        который находится на экране*/

        WebDriver driver;

        @BeforeClass
        public void getStartPate(){
            System.setProperty("webdriver.chrome.driver", "C:\\Repository\\Rep1\\chromedriver.exe");
            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            String lunaLoginIRL = "https://192.168.222.20:9000/login";
            driver.get(lunaLoginIRL);
            System.out.println("Переходим на страницу логина");


            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            HttpPost requestPOST = new HttpPost("http://192.168.222.20:8888/3/accounts"); //Объект для отправки POST запроса. Для каждого API есть документация. В ней я узнал, чтобы создать пользователя, необходимо отправить POST запрос на этот адрес с нужными данными


            try{
                JSONObject json = new JSONObject(); //Создаём JSON и заполняем его нужным для создания пользователя данными

                json.put("organization_name", orgnizationName); //Название организации из константы класса InitClass
                json.put("email", loginName); //Имя пользователя из константы класса InitClass
                json.put("password",password); //Пароль пользователя из константы класса InitClass
                // StringEntity params = new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");

                StringEntity params = new StringEntity(json.toString()); //Этот объект нужен, чтобы засунуть данные JSON'a в POST запрос
                requestPOST.addHeader("content-type", "application/json"); //Устанавливаем заголовок, в котором указываем, что данные отправляютс через JSON (про этот заголовок можно погуглить и узнать, какие тут вообще бывают значения
                requestPOST.setEntity(params); //Засовываем данные в POST запрос


                HttpResponse response = httpClient.execute(requestPOST); //Выполняем POST запрос и получаем данные

                System.out.println("Отправка POST " + response.getStatusLine() + " "+ requestPOST + "\n" + requestPOST.getEntity());
                System.out.println("Отправка POST " + response.getEntity());

                HttpEntity entity = response.getEntity(); //Этот объект нужен, чтобы вытащить данные из response
                String responseString = EntityUtils.toString(entity, "UTF-8"); //Тут мы преобразовываем все данные из response в строку. Скорее всего она будет в формате JSON
                System.out.println(responseString);

                JSONParser parser = new JSONParser(); //Этот объект нужен, чтобы сконвертировать строку в JSON объект (этого можно было и не делать, но так удобнее потом работать с ответом
                JSONObject jsonOBJ = (JSONObject) parser.parse(responseString);
                tokenAPI = jsonOBJ.get("token").toString(); //Как раз то самое удобство. Из документации можно выяснить, что они должны вернуть тег "token". И благодаря тому, что сконвертировали строку в JSON объект, мы можем получать данные таким способом (иначе пришлось бы парсить строку)

                System.out.println("Это ТОКЕН, полученный через API " + jsonOBJ.get("token"));

            }catch(Exception ex){
                System.out.println("Ошибка в отправке POST " + ex);
            }

        }


        @Test
        public void tokenTest(){


            //WebElement emailElement = driver.findElement(By.className("container form_login")); //Ошибка Compound class names not permitted

            //Ну а это стандартный PageObject. Тут и пояснять нечего, сама лучше меня знаешь :)
            LoginLunaPage loginPage = new LoginLunaPage(driver);
            loginPage.login(loginName, password);

            driver = loginPage.getDriver();

            ArrayPage arrayPage = new ArrayPage(driver);
            arrayPage.findTokens();

            TokenPage tokenPage = new TokenPage(driver);
            String tokenInterface = tokenPage.getTokenValue();
            System.out.println("ЭТО токен, полученынй через интерфейс " + tokenPage.getTokenValue());

            assertEquals(tokenAPI,tokenInterface);


        }

        @Test
        public void CreateToken(){
            String createTokenURL = "http://192.168.222.20:8888/3/account/tokens";

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost requestPOST = new HttpPost(createTokenURL);

            try{
                JSONObject json = new JSONObject(); //Создаём JSON и заполняем его нужным для создания пользователя данными

                json.put("token_data", "somedata"); //Название организации из константы класса InitClass
                json.put("client_id", tokenAPI);


                StringEntity params = new StringEntity(json.toString()); //Этот объект нужен, чтобы засунуть данные JSON'a в POST запрос
                requestPOST.addHeader("content-type", "application/json"); //Устанавливаем заголовок, в котором указываем, что данные отправляютс через JSON (про этот заголовок можно погуглить и узнать, какие тут вообще бывают значения
                requestPOST.setEntity(params); //Засовываем данные в POST запрос


                HttpResponse response = httpClient.execute(requestPOST); //Выполняем POST запрос и получаем данные

                System.out.println("Отправка POST " + response.getStatusLine() + " "+ requestPOST + "\n" + requestPOST.getEntity());
                System.out.println("Отправка POST " + response.getEntity());

                HttpEntity entity = response.getEntity(); //Этот объект нужен, чтобы вытащить данные из response
                String responseString = EntityUtils.toString(entity, "UTF-8"); //Тут мы преобразовываем все данные из response в строку. Скорее всего она будет в формате JSON
                System.out.println(responseString);

                JSONParser parser = new JSONParser(); //Этот объект нужен, чтобы сконвертировать строку в JSON объект (этого можно было и не делать, но так удобнее потом работать с ответом
                JSONObject jsonOBJ = (JSONObject) parser.parse(responseString);

                System.out.println(jsonOBJ.toString());

            }catch(Exception ex){
                System.out.println("Ошибка в отправке POST " + ex);
            }


        }

        @AfterClass
        public void CloseDriver() throws InterruptedException {
            Thread.sleep(5000);
            driver.quit();



            System.out.println("Изменяем тестовые данные");

        }

    }


