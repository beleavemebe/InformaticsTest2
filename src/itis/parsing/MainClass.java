package itis.parsing;


/*
Вы пишите систему для интеграции с готовой структурой заказчика и обработки данных о парках и зонах отдыха.
!!КЛАСС PARK менять НЕЛЬЗЯ (совсем). Считается что он является частью готовой библиотеки!!

 * В папке resources находятся два файла которые содержат примеры данных, которые нужно обрабатывать.
 * Данные записаны в формате одна строка - одно поле. Название поля и значение разделяются символом ":".
 * Над некоторыми полями стоят аннотации, которые служат для проверки даннных поля. Подробное описание доступно в классе аннотации.
___________________
 * Напишите код, который превратит содержимое файлов в обьекты класса "Park", учитывая аннотации над полями.
 * Парсинг должен учитывать следующие моменты:
 * 1. Парсятся значения только известных полей (которые есть в классе или упомянуты в аннотациях), все остальное игнорируется
 * 2. "null" должен распознаваться как null
 * 3. Ограничения аннотаций должны обрабатываться с помощью рефлексии
 * 4. Если файл не прошел проверку, должна формироваться ошибка с информативным содержанием (см. ниже)
___________________
 Обработка ошибок:
 Если при попытке парсинга файла возникли ошибки проверки (не соблюдаются ограничения аннотаций), должно выбрасываться ParkParsingException.
 Внутри должны быть перечислены все поля, при обработке которых возникли ошибки, и описание ошибки (напр. "Поле не может быть пустым")
 См. класс "ParkParsingException" для доп. информации.
___________________
Обработка аннотаций и формирование ошибок оцениваются отдельно.
 * */
public class MainClass {

    private ParkParsingServiceImpl parsingService = new ParkParsingServiceImpl();

    public static void main(String[] args) throws Exception {
        new MainClass().run("src/itis/parsing/resources/parkdata");
    }

    private void run(String parkFilePath) {
        Park park = null;
        try {
            park = parsingService.parseParkData(parkFilePath);
        } catch (ParkParsingException parsingException) {
            System.out.println(parsingException.getValidationErrors());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        System.out.println(park);

    }

}










