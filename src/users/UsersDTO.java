package users;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class UsersDTO {

    @NonNull
    private String user_id;
    @NonNull
    private String password;
    @NonNull
    private int age;

}
