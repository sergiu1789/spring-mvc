package com.example.springmvc.controllers;


import com.example.springmvc.models.User;
import com.example.springmvc.repositories.UserRepository;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.example.springmvc.controllers.UserController.Routes.*;

@Controller
public class UserController {
    @Autowired
    private UserRepository repository;


    @GetMapping
    public String getIndexPage(Model model) {
        /*
        Model e interfata de spring care imi asigua un mecanisc de a transmite informatii (Date)  pe structura key value catre tamplateu de html unde este extrasa inf pe baza jey
         */
        List<User> users = repository.findAll();
        if (!users.isEmpty()) {
            model.addAttribute("users", users);
        }
        return INDEX;

    }

    @GetMapping("/signup")
    public String getSignUpPage(User user) {
        return ADD_USER;

    }


    //    }
    @PostMapping("/add-user")
    public String createNewUser(@Valid User user, BindingResult result) {
        //@Valid -valideaza user
        //BindingResult interfata s[ring care contine  (in sit in care exista) err de validare (vezo adnotarile @notbalcn din clasa user)
        //in sit in care am errori nu continui cu salvarea ci reintorc user spre pagina initiala unde in interiorul span (html) voi popula cu textul errorei fo
        //folosind numele proprietari

        if (result.hasErrors()) {
            return ADD_USER;
        }
        //daca nu gasesc erori incep sa salvez

        repository.save(user);


        //este un bug  daca dau "redirect:"aici merge fara / care in delete nu functioneaza
        return REDIRECT;
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable("id") Long id) {

        repository.deleteById(id);

        //redirect imi apeleaza programatic endpointul mapat la / in controlar
        //adnotarea @GetMapping ar trebui sa mapeze acea adresa ,deci redirectul ma duce la pagina de index unde
        //am deja un model care este populat
        return REDIRECT + "/";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable("id") Long id, Model model) {
        //vreau sa extrag un user din baza de date
   /*     Optional<User> userOptional=repository.findById(id);
        User existingUser;
        if(userOptional.isPresent() ){
            existingUser=userOptional.get();

        }
        else {
            throw new IllegalArgumentException(String.format("Missing user with id %s" ,id));
        }
        */
        User existingUser = repository.findById(id).<IllegalArgumentException>orElseThrow(() -> {
            throw new IllegalArgumentException(String.format("Missing user with id: %s", id));
        });
        model.addAttribute("user", existingUser);
        return UPDATE_USER;


    }

    @PostMapping("/update/{id}")
    public String updateAndReturnToIndex(@PathVariable("id") Long id,
                                         @Valid User user, BindingResult result) {
        if (result.hasErrors()) {

            return UPDATE_USER;
        }
        repository.save(user);
        return REDIRECT + "/";
    }


    // Am creat o clasa statica cu constante acestuia(rute) deoarece sunt folosite in mai mult de un loc si numele fisierului se schimba
    //atunci e mai simplu si sa sigur sa updatez intr-un singur loc
    static class Routes {
        static final String INDEX = "index";
        static final String ADD_USER = "add-user";
        static final String UPDATE_USER = "update_user";
        static final String REDIRECT = "redirect:";


    }
}

