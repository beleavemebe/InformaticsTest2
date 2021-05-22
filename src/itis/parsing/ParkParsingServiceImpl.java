package itis.parsing;

import itis.parsing.annotations.FieldName;
import itis.parsing.annotations.MaxLength;
import itis.parsing.annotations.NotBlank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

public class ParkParsingServiceImpl implements ParkParsingService {

    //Парсит файл в обьект класса "Park", либо бросает исключение с информацией об ошибках обработки
    @Override
    public Park parseParkData(String parkDatafilePath) throws ParkParsingException {
        File dataSource = new File(parkDatafilePath);
        return parsePark(dataSource);
        //write your code here
    }

    private Park parsePark(File data) throws ParkParsingException {
        try {
            Constructor<Park> declaredConstructor = Park.class.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Park park = declaredConstructor.newInstance();
            initializeFields(park.getClass().getDeclaredFields(), park, data);
            return park;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private void initializeFields(Field[] fields, Park park, File dataSource) throws ParkParsingException, ReflectiveOperationException {
        List<ParkParsingException.ParkValidationError> errors = new ArrayList<>();
        Arrays.stream(fields).forEach(field -> {
            String name = field.getName();
            FieldName annoFieldName = field.getAnnotation(FieldName.class);
            if (annoFieldName != null) {
                name = annoFieldName.value();
            }
            String value = findValueForField(name, dataSource);
            if (value.equals("null") | value.equals("")) {
                NotBlank annoNotBlank = field.getAnnotation(NotBlank.class);
                if (annoNotBlank != null) {
                    errors.add(new ParkParsingException.ParkValidationError(name, "Поле не должно пустым"));
                    value = null;
                }
            } else if (field.getType().equals(String.class)) {
                MaxLength annoMaxLength = field.getAnnotation(MaxLength.class);
                if (annoMaxLength != null) {
                    int maxLength = annoMaxLength.value();
                    if (value.length() > maxLength) {
                        value = value.substring(0 , maxLength);
                        errors.add(new ParkParsingException.ParkValidationError(name, "Слишком длинное значение"));
                    }
                }
            }
            try {
                field.setAccessible(true);
                if (field.getType().equals(LocalDate.class)) {
                    if (value != null) field.set(park, LocalDate.parse(value));
                } else {
                    field.set(park, value);
                }
            } catch (IllegalAccessException cannotHappen) {}
        });
        if (errors.size() != 0) {
            throw new ParkParsingException("Произошла ошибка", errors);
        }
    }

    private String findValueForField(String fieldName, File dataSource) {
        try {
            Scanner scanner = new Scanner(dataSource);
            scanner.useDelimiter("\n");
            String s1 = scanner.tokens().filter(s -> {
                if (s.equals("***")) return false;
                String str = s.split(":")[0];
                str = str.trim().substring(1, str.lastIndexOf('"'));
                return str.equals(fieldName);
            }).toArray(String[]::new)[0].split(":")[1].trim();
            if (s1.contains("\"")){
                return s1.trim().substring(1, s1.lastIndexOf("\""));
            }
            return s1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
