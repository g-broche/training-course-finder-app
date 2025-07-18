package com.example.finder.utils;

import com.example.finder.dto.input.RequestRegister;
import com.example.finder.dto.output.ErrorDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ValidatorUtil {
    private final int UserNameMin = 1;
    private final int UserNameMax = 30;
    private final int passwordMin = 8;
    private String userNameRegex;
    private final String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,4}$";

    ValidatorUtil(){
        // using %d has an integer positional format specifier for placeholder to
        // replace by two following variables when building the string
        this.userNameRegex = String.format("^[\\p{L} .'-]{%d,%d}$", UserNameMin, UserNameMax);
    }

    public boolean isValidFirstName(String input) {
        System.out.println(">>> firstName : "+input);
        if(input == null) {return false;}
        String trimmedInput = input.trim();
        return trimmedInput.matches(userNameRegex);
    }
    public boolean isValidLastName(String input) {
        System.out.println(">>> lastName : "+input);
        if(input == null) {return false;}
        String trimmedInput = input.trim();
        return trimmedInput.matches(userNameRegex);
    }
    public boolean isValidDisplayName(String input) {
        System.out.println(">>> displayName : "+input);
        if(input == null) {return false;}
        String trimmedInput = input.trim();
        return trimmedInput.matches(userNameRegex);
    }
    public boolean isValidEmail(String input) {
        System.out.println(">>> email : "+input);
        if(input == null) {return false;}
        String trimmedInput = input.trim();
        return trimmedInput.matches(emailRegex);
    }
    public boolean isValidPassword(String input) {
        System.out.println(">>> password : "+input);
        if(input == null) {return false;}
        return input.trim().length() >= passwordMin;
    }

    /**
     * For all inputs in a RequestRegister this method applies validation and will
     * return a list of all errors.
     * @param inputs RequestRegister instance representing signup data
     * @return List of error strings or empty list if no error
     */
    public List<ErrorDto> validateRegisterInputs(RequestRegister inputs){
        List<ErrorDto> errors = new ArrayList<>();
        if(!isValidFirstName(inputs.getFirstName())){
            errors.add(new ErrorDto(
                    "firstName",
                    "first name must have between "+UserNameMin+" and "+UserNameMax+" characters"
            ));
        }
        if(!isValidLastName(inputs.getLastName())){
            errors.add(new ErrorDto(
                    "lastName",
                    "last name must have between "+UserNameMin+" and "+UserNameMax+" characters"
            ));
        }
        if(!isValidDisplayName(inputs.getDisplayName())){
            errors.add(new ErrorDto(
                    "displayName",
                    "display name must have between "+UserNameMin+" and "+UserNameMax+" characters"
            ));
        }
        if(!isValidEmail(inputs.getEmail())){
            errors.add(new ErrorDto(
                    "email",
                    "email format is invalid"
            ));
        }
        if(!isValidPassword(inputs.getPassword())){
            errors.add(new ErrorDto(
                    "password",
                    "password must have at least "+passwordMin+" characters"
            ));
        }
        return errors;
    }
}
