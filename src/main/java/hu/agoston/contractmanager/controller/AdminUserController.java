package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.CreateUserRequest;
import hu.agoston.contractmanager.dto.UserResponse;
import hu.agoston.contractmanager.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Valid @RequestBody CreateUserRequest request) {
        adminUserService.createUser(request);
    }
}
