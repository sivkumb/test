package com.example.demo;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siva on 9/17/2017.
 */

@RestController
public class JsonController {

    @RequestMapping("/hello")
    public ResponseEntity<Map> test() throws Exception
    {

        String jsonData = readFIle("/test.json");

        Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonData);

        String author0 = JsonPath.read(document, "$.store.book[0].author");
        String author1 = JsonPath.read(document, "$.store.book[1].author");
        String bcolor = JsonPath.read(document, "$.store.bicycle.color");
        int expense = JsonPath.read(document, "$.expensive");

        double price = JsonPath.read(document, "$.store.bicycle.price");

        System.out.println("Author1 :"+ author0);
        System.out.println("Author2s :"+ author1);
        System.out.println("bColor : "+ bcolor);
        System.out.println("bColor : "+ price);

        System.out.println("expensive : "+ expense);

        Map<String, Object> myMap = new HashMap<>();

        MapUtil.add2Map(myMap, "key1", "value1", false);
        MapUtil.add2Map(myMap, "key2", "value2", false);

        MapUtil.add2Map(myMap, "key2.key3.key4.key5.key6", "value32", true);

        return new ResponseEntity<Map>(myMap, HttpStatus.CREATED);
    }


    public String  readFIle(String fileName) throws FileNotFoundException, IOException
    {
        InputStream in = getClass().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        System.out.println(out.toString());   //Prints the string content read from input stream
        reader.close();
        return out.toString();
    }

}




class MapUtil
{
    static void add2Map(Map<String, Object> map,  String keyPath, Object value, boolean isList) {

        String[] keys = keyPath.split("\\.");
        recursiveAdd(map,keys, value, 0 , isList);

    }

    private static void recursiveAdd(Map<String, Object> map, String[] keys, Object value, int index, boolean isList)
    {
        if(index == keys.length-1)
        {
            Object existingValue = map.get(keys[index]);

            if((existingValue != null)  && (existingValue instanceof List )) {
                    List values = (List) existingValue;
                    values.add(value);
                    return;
            }

              if(isList)
               {
                   List myList = new ArrayList();
                   if(existingValue != null) {
                       myList.add(existingValue);
                   }
                   myList.add(value);
                   map.put(keys[index], myList);
                   return;
               }

            map.put(keys[index], value);
            return  ;
        }

        if(!map.containsKey(keys[index]) || !(map.get(keys[index]) instanceof  Map) )
        {
            map.put(keys[index], new HashMap<String, Object>() );
        }

        Map<String, Object> myMap = ( Map<String, Object>)map.get(keys[index]);
        map.put(keys[index], myMap);

        recursiveAdd(myMap, keys, value, index+1, isList);
    }

}


