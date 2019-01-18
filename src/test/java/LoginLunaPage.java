package HTTP;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginLunaPage {

    //Первая страница, в которой вводятся данные для входа и происходит логин
    WebDriver driver;

    public LoginLunaPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String loginName, String password){
        WebElement emailElement = driver.findElement(By.xpath("//form[@class='container form_login']/input[1]"));
        emailElement.sendKeys(loginName);

        WebElement passwordElement = driver.findElement(By.xpath("//form[@class='container form_login']/input[2]"));
        passwordElement.sendKeys(password);

        WebElement signElement = driver.findElement(By.xpath("//form[@class='container form_login']/input[3]"));
        signElement.click();
    }


    public WebDriver getDriver() {
        return driver;
    }
}
