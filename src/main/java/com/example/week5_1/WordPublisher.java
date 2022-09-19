package com.example.week5_1;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class WordPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    protected Word words = new Word();

    @RequestMapping(value = "/addBad/{word}" , method = RequestMethod.GET)
    public ArrayList<String> addBadWord(@PathVariable("word") String s) {
        words.badWords.add(s);
        return words.badWords;
    }
    @RequestMapping(value = "/delBad/{word}" , method = RequestMethod.GET)
    public ArrayList<String> deleteBadWord(@PathVariable("word") String s){
        words.badWords.remove(s);
        return words.badWords;
    }
    @RequestMapping(value = "/addGood/{word}" , method = RequestMethod.GET)
    public ArrayList<String> addGoodWord(@PathVariable("word") String s){
        words.goodWords.add(s);
        return words.goodWords;
    }
    @RequestMapping(value = "/delGood/{word}" , method = RequestMethod.GET)
    public ArrayList<String>  deleteGoodWord(@PathVariable("word") String s){
        words.goodWords.remove(s);
        return words.goodWords;
    }
    @RequestMapping(value = "/proofSentence/{sentence}" , method = RequestMethod.GET)
    public String proofSentence(@PathVariable("sentence") String s){
        boolean isFoundBad = false;
        boolean isFoundGood = false;

        for(int i=0;i< words.badWords.size(); i++) {
             isFoundBad = s.contains(words.badWords.get(i));
             if (isFoundBad){
                 break;
             }

        }
        for(int j=0;j< words.goodWords.size(); j++) {
             isFoundGood = s.contains(words.goodWords.get(j));
            if (isFoundGood){
                break;
            }
        }
        if(isFoundBad && isFoundGood){
            rabbitTemplate.convertAndSend("Fanout","",s );
            return "Found Bad & Good word";
        }
        else if(isFoundBad){
            rabbitTemplate.convertAndSend("Direct","bad",s );
            return "Found Bad Words";
        }
         else if(isFoundGood){
            rabbitTemplate.convertAndSend("Direct","good",s );
            return "Found Good Words";
        }

        return "Not Found";
    }
}
