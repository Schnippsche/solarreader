import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.StandardValues;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.TableField;
import de.schnippsche.solarreader.backend.fields.TableFieldType;
import de.schnippsche.solarreader.backend.utils.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SolarreaderTest
{
  @Test void testSpecifications()
  {
    Pattern pattern = Pattern.compile("[a-zA-z]+[\\w]+");
    JsonTools jsonTools =  new JsonTools();
    // Test all specifications
    File folder = new File("./src/main/resources/specifications");
    StandardValues standardValues = Config.getInstance().getStandardValues();
    standardValues.setDateAndTimeValues();
    File[] listOfFiles = folder.listFiles();
    boolean result = true;
    assert listOfFiles != null;
    for (File file : listOfFiles)
    {
      if (file.isFile() && file.getName().endsWith(".json"))
      {
        String resource = file.getName().replace(".json", "");
        System.out.println(resource);
        Specification specification = jsonTools.readSpecification(resource);
        List<DeviceField> deviceFields = specification.getDevicefields();
        for (DeviceField deviceField : deviceFields)
        {
          // check all table fields
          if (deviceField.getOffset() < 0 || deviceField.getName() == null || deviceField.getName().contains(" ")
              || deviceField.getName().isEmpty() || deviceField.getCount() < 1 || deviceField.getType() == null
              || deviceField.getType().toString().isEmpty())
          {
            System.out.println("invalid device field in resource " + resource + ":" + deviceField);
            result = false;
          }
          if ((deviceField.getType() == FieldType.U16_BIG_ENDIAN || deviceField.getType() == FieldType.I16_BIG_ENDIAN
               || deviceField.getType() == FieldType.I16_LITTLE_ENDIAN
               || deviceField.getType() == FieldType.U16_LITTLE_ENDIAN) && deviceField.getCount() != 1)
          {
            System.out.println("Länge passt nicht zu Datenfeld X16 in resource " + resource + ":" + deviceField);
            result = false;
          }
          if ((deviceField.getType() == FieldType.U32_BIG_ENDIAN || deviceField.getType() == FieldType.I32_BIG_ENDIAN
               || deviceField.getType() == FieldType.U32_MIXED_ENDIAN
               || deviceField.getType() == FieldType.U32_LITTLE_ENDIAN
               || deviceField.getType() == FieldType.I32_LITTLE_ENDIAN) && deviceField.getCount() != 2)
          {
            System.out.println("Länge passt nicht zu Datenfeld X32 in resource " + resource + ":" + deviceField);
            result = false;
          }
          if ((deviceField.getType() == FieldType.U64_BIG_ENDIAN || deviceField.getType() == FieldType.I64_BIG_ENDIAN)
              && deviceField.getCount() != 4)
          {
            System.out.println("Länge passt nicht zu Datenfeld X64 in resource " + resource + ":" + deviceField);
            result = false;
          }
          if ((deviceField.getType() == FieldType.SCALEFACTOR_BIG_ENDIAN) && deviceField.getCount() != 1)
          {
            System.out.println("Länge passt nicht zu Datenfeld X8 in resource " + resource + ":" + deviceField);
            result = false;
          }
          if (!deviceField.getName().matches("\\w+"))
          {
            System.out.println("invalid fieldname in resource " + resource + ":" + deviceField.getName());
          }
        }

        List<TableField> tableFields = specification.getDatabasefields();
        // Doppelte Table Fields
        Set<TableField> items = new HashSet<>();
        Set<TableField> duplikate = tableFields.stream()
                                               .filter(n -> !items.add(n)) // Set.add() returns false if the element was already in the set.
                                               .collect(Collectors.toSet());
        if (!duplikate.isEmpty())
        {

          for (TableField field : duplikate)
          {
            System.err.println("duplicate tablefield in resource " + resource + ":" + field);
          }

          result = false;
        }

        for (TableField tableField : tableFields)
        {
          // Check all tablefields
          if (tableField.getTablename() == null || tableField.getTablename().isEmpty()
              || "XXXXX".equals(tableField.getTablename()) || tableField.getColumntype() == null
              || tableField.getColumntype().toString().isEmpty() || tableField.getColumnname() == null
              || tableField.getColumnname().isEmpty() || tableField.getSourcetype() == null
              || tableField.getSourcevalue() == null || tableField.getSourcevalue().isEmpty())
          {
            System.err.println("invalid TableField in resource " + resource + ":" + tableField);
            result = false;
          }
          // is table field in device field ( only if devicelist is not empty! )
          if (!deviceFields.isEmpty())
          {
            if (tableField.getSourcetype() == TableFieldType.RESULTFIELD)
            {
              String resultfieldname = tableField.getSourcevalue();
              long count = deviceFields.stream().filter(df -> df.getName().equals(tableField.getSourcevalue())).count();
              if (count == 0)
              {
                System.out.println(
                  "Warning:Table Field ist not in devicefield declarations in resource " + resource + ":"
                  + tableField.getSourcevalue());
              }
            }
            if (tableField.getSourcetype() == TableFieldType.STANDARDFIELD)
            {
              if (standardValues.getValue(tableField.getSourcevalue()) == null)
              {

                System.err.println("Error:Table Field does not exist in standard values " + resource + ":"
                                   + tableField.getSourcevalue());
                result = false;
              }
            }
            if (tableField.getSourcetype() == TableFieldType.CALCULATED)
            {
              String source = tableField.getSourcevalue();
              Matcher m = pattern.matcher(source);
              while (m.find())
              {
                String variable = m.group();
                int idx = variable.indexOf("_UBYTE");
                if (idx > 0)
                {
                  variable = variable.substring(0, idx);
                }
                idx = variable.indexOf("_IBYTE");
                if (idx > 0)
                {
                  variable = variable.substring(0, idx);
                }
                //
                String finalVariable = variable;
                long count = deviceFields.stream().filter(df -> df.getName().equals(finalVariable)).count();
                if (count == 0)
                {
                  System.out.println(
                    "Warning:Table Field Variable does not exist in device fields in resource " + resource + ":"
                    + variable);
                }
              }
            }
          }
        }
      }
    }

    Assertions.assertTrue(result);
  }

}
