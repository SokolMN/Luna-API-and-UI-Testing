package HTTP;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ArrayPage {
    WebDriver driver;

    //Вторая страница, в которой находятся разные данные (набор токенов, набор твоих фото и тд)

    public ArrayPage(WebDriver driver) {
        this.driver = driver;

    }

    public void findTokens()  {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement tokensElement = driver.findElement(By.xpath(".//*[text()='tokens']/.."));
        tokensElement.click();
    }
}
