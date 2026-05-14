package ch.portami.inventorybackend.offer.api;

import ch.portami.inventorybackend.offer.CustomerService;
import ch.portami.inventorybackend.offer.dto.CreateCustomerDto;
import ch.portami.inventorybackend.offer.dto.CustomerDto;
import ch.portami.inventorybackend.offer.dto.UpdateCustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customers", description = "Manage customers")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "List customers")
    @ApiResponse(responseCode = "200", description = "List of customers")
    @GetMapping
    public ResponseEntity<List<CustomerDto>> listCustomers() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @Operation(summary = "Create a customer")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CreateCustomerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(customerService.createCustomer(dto));
    }

    @Operation(summary = "Patch a customer")
    @ApiResponse(responseCode = "200", description = "Customer updated")
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @RequestBody UpdateCustomerDto dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }
}

