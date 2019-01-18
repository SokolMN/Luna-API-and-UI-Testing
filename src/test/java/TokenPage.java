package HTTP;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Hashtable;
import java.util.List;

public class TokenPage {

    //Третья страница, в которой находится список токенов
    WebDriver driver;

    public TokenPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTokenValue(){
        WebElement tokenValueElement = driver.findElement(By.xpath("//div[@class='token_container']/p[1]"));
        return tokenValueElement.getText();
    }

    public String getNewLastTokenValue(){
        System.out.println("Обновляю экран");
        driver.navigate().refresh();
        System.out.println("Обновил экран");


        System.out.println("Ищу токены");
        List<WebElement> allTokens = driver.findElements(By.xpath("//div[@class='token_container']"));
        System.out.println("Нашел вот столько токенов " + allTokens.size());
        System.out.println("Первый токен: " + allTokens.get(0).getText());
        System.out.println("Второй токен: " + allTokens.get(1).getText());

        return allTokens.get(1).getText();
    }

    public Hashtable getAllTokenInfo(){
        Hashtable tokenTable = new Hashtable();

        System.out.println("Начинаю собирать информацию по токенам");
        List<WebElement> allTokens = driver.findElements(By.xpath("//div[@class='token_container']"));

        System.out.println("Первый токен: " + allTokens.get(0).findElement(By.xpath("./p[@class='token_id']")).getText());
        System.out.println("Первый токен: " + allTokens.get(0).findElement(By.xpath("./p[@class='description_block']")).getText());

        System.out.println("Второй токен: " + allTokens.get(1).findElement(By.xpath("./p[@class='token_id']")).getText());
        System.out.println("Второй токен: " + allTokens.get(1).findElement(By.xpath("./p[@class='description_block']")).getText());

        for(int i=0; i < allTokens.size(); i = i + 1){
            System.out.println("Id токена " + allTokens.get(i).findElement(By.xpath("./p[@class='token_id']")).getText());
            System.out.println("Описание токена " + allTokens.get(i).findElement(By.xpath("./p[@class='description_block']")).getText());
            tokenTable.put(allTokens.get(i).findElement(By.xpath("./p[@class='token_id']")).getText(), allTokens.get(i).findElement(By.xpath("./p[@class='description_block']")).getText());
        }


        return tokenTable;
    }
}
