package com.nikiforov;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.ProjConfig;
import helpers.Attach;
import io.qameta.allure.Owner;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@DisplayName("Первая работа с Jenkins")
@Tag("first_properties_task")
public class TelegramNotificationsDemoqaTest {
    @BeforeAll
    static void beforeAll() {

        System.setProperty("environment", System.getProperty("environment", "prod"));

        Configuration.baseUrl = "https://demoqa.com";
        Configuration.pageLoadStrategy = "normal";
        Configuration.timeout = 5000;
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserVersion = System.getProperty("browserVersion", "100.0");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.remote = System.getProperty("browserRemoteUrl");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;
    }

    @BeforeEach
    void beforeEach() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    void addAttachments() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();
    }

    @Test
    @Owner("Nikiforov")
    @DisplayName("Заполнение и проверка формы на сайте demoqa")
    void fillRegistrationForm() {
        ProjConfig projConfig = ConfigFactory.create(ProjConfig.class);

        step("Открываем раздел automation-practice-form", ()-> {
            open("/automation-practice-form");

            SelenideElement bannerRoot = $(".fc-consent-root");
            if (bannerRoot.isDisplayed()) {
                bannerRoot.$(byText("Consent")).click();
            }

            executeJavaScript("$('#fixedban').remove()");
            executeJavaScript("$('footer').remove()");
        });

        step("Заполняем форму", ()-> {
            $("#firstName").setValue(projConfig.firstName());
            $("#lastName").setValue(projConfig.lastName());
            $("#userEmail").setValue("unleash21@mail.ru");
            $("#genterWrapper").$(byText("Male")).click();
            $("#userNumber").setValue("8999777665");
            $("#dateOfBirthInput").click();
            $(".react-datepicker__month-select").click();
            $("[value='10']").click();
            $(".react-datepicker__year-select").click();
            $("[value='1989']").click();
            $(".react-datepicker__month").$(byText("20")).click();
            $("#subjectsInput").setValue("Eng");
            $("#subjectsWrapper").$(byText("English")).click();
            $("#subjectsInput").setValue("Ma");
            $("#subjectsWrapper").$(byText("Maths")).click();
            $("#hobbiesWrapper").$(byText("Sports")).click();
            $("#hobbiesWrapper").$(byText("Reading")).click();
            $("#uploadPicture").uploadFromClasspath("image.jpg");
            $("#currentAddress").setValue("Lorem ipsum dolor");
            $("#stateCity-wrapper").$(byText("Select State")).click();
            $("#stateCity-wrapper").$(byText("NCR")).click();
            $("#stateCity-wrapper").$(byText("Select City")).click();
            $("#stateCity-wrapper").$(byText("Delhi")).click();
            $("#submit").click();
        });

        step("Проверяем результат", ()-> {
            $(".modal-content").shouldHave(text("Thanks for submitting the form"));
            $(".modal-body").shouldHave(text(projConfig.firstName()));
            $(".modal-body").shouldHave(text(projConfig.lastName()));
            $(".modal-body").shouldHave(text("unleash21@mail.ru"));
            $(".modal-body").shouldHave(text("Male"));
            $(".modal-body").shouldHave(text("8999777665"));
            $(".modal-body").shouldHave(text("20 November,1989"));
            $(".modal-body").shouldHave(text("English, Maths"));
            $(".modal-body").shouldHave(text("Sports, Reading"));
            $(".modal-body").shouldHave(text("image.jpg"));
            $(".modal-body").shouldHave(text("Lorem ipsum dolor"));
            $(".modal-body").shouldHave(text("NCR Delhi"));
        });
    }
}