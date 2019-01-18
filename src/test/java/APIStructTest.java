package HTTP;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class APIStructTest extends InitClass{

    WebDriver driver;
   // String tokenWEB; //Токен полученный через интерфейс
    APIWorker apiWorker = new APIWorker();
    Hashtable tokensTableAPI; // Список токенов из API (ключ - Id, значение - token data)
    Hashtable tokensTableWEB; //Список токенов из интерфейса(ключ - Id, значение - token data)

    @BeforeClass
    public void getStartPate() {
        System.setProperty("webdriver.chrome.driver", "C:\\Repository\\Rep1\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String lunaLoginIRL = "https://192.168.222.20:9000/login";
        driver.get(lunaLoginIRL);
        System.out.println("Переходим на страницу логина");
    }

    @Test
    public void tokenTest(){


        createAccountAPI(); //Создаем токен через API



        loginLunaWeb(); //Логинемся в систему LUNA через WEB
        chooseTokenArrayWeb();//Выбираем список токенов в WEB
        getTokenWeb();//Получаем значение токена из WEB


        System.out.println("ЭТО токен, полученынй через интерфейс " + tokenWEB);

        assertEquals(tokenAPI,tokenWEB);

        System.out.println("Создаем новый токен в API");
        createTokenForCurrentAccountAPI();

        System.out.println("Ищем новый токен в WEB");
        getNewTokenForCurrentAccountWEB();


        //Так как у меня кривой xpath надо подредактировать полученный через WEB токен
        //В дальнейшем необходимо изменить xpath для получения второго токена
        tokenWEB = tokenWEB.substring(0, tokenWEB.indexOf('\n'));
        System.out.println("ЭТО токен, полученынй через интерфейс " + tokenWEB);
        System.out.println("ЭТО токен, полученынй через API " + tokenAPI);
        assertEquals(tokenAPI,tokenWEB);

        System.out.println("Запускаем шаг с запросом GET");
        getTokensForCurrentAccountAPI();

        System.out.println("Запускаем шаг с получение из WEB всех токенов");
        getTokensForCurrentAccountWEB();
        System.out.println("Сравнение hashTable: " + tokensTableWEB.equals(tokensTableAPI));

        System.out.println("Table токенов из WEB: " + tokensTableWEB.toString());
        System.out.println("Table токенов из API: " + tokensTableAPI.toString());
        assertEquals(tokensTableWEB.equals(tokensTableAPI),true);
    }

    @Step
    public void getTokensForCurrentAccountWEB(){
        TokenPage tokenPage = new TokenPage(driver);
        tokensTableWEB = tokenPage.getAllTokenInfo();
    }

    @Step
    public void getTokensForCurrentAccountAPI(){
        apiWorker.setUrl("http://192.168.222.20:5000/3/account/tokens");
        tokensTableAPI = apiWorker.getTokensOwnedByCurrentAccountAPI();
    }

    @Step
    public void createAccountAPI(){
        apiWorker.setUrl("http://192.168.222.20:5000/3/accounts");
        apiWorker.createAccountAPI();
    }

    @Step
    public void loginLunaWeb(){
        LoginLunaPage loginPage = new LoginLunaPage(driver);
        loginPage.login(loginName, password);
        driver = loginPage.getDriver();
    }

    @Step
    public void chooseTokenArrayWeb(){
        ArrayPage arrayPage = new ArrayPage(driver);
        arrayPage.findTokens();
    }

    @Step
    public void getTokenWeb(){
        TokenPage tokenPage = new TokenPage(driver);
        tokenWEB = tokenPage.getTokenValue();
    }

    @Step
    public void createTokenForCurrentAccountAPI(){
        apiWorker.setUrl("http://192.168.222.20:5000/3/account/tokens");
        apiWorker.createTokenForCurrentAccountAPI();
        tokenAPI = apiWorker.getToken();

    }


    @Step
    public void getNewTokenForCurrentAccountWEB(){
        TokenPage tokenPage = new TokenPage(driver);
        tokenPage.getNewLastTokenValue();
        tokenWEB = tokenPage.getNewLastTokenValue();
    }

    @AfterClass
    public void CloseDriver() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }
}
