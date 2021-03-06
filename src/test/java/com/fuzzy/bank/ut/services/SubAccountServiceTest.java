package com.fuzzy.bank.ut.services;

import com.blueharvest.bank.dto.*;
import com.fuzzy.bank.services.CustomerService;
import com.fuzzy.bank.services.SubAccountService;
import com.fuzzy.bank.services.TransactionService;
import com.fuzzy.bank.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubAccountServiceTest {
    @Mock
    private TransactionService transactionService;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private SubAccountService subAccountService;

    CustomerDto customerDto=new CustomerDto();
    TransactionDto transactionDto= new TransactionDto();
    SubAccountDto subAccountDto= new SubAccountDto();
    RequestAccountDto requestAccountDto= new RequestAccountDto();


    @BeforeEach
    void setUp() {
        customerDto.setBalance(5000.0);
        customerDto.setName("fathy");
        customerDto.setSurName("elshemy");
        customerDto.setId(1L);

        subAccountDto.setCustomer(customerDto);
        subAccountDto.setBalance(500.0);

        transactionDto.setAmount(500.0);
        transactionDto.setSubAccount(subAccountDto);
        transactionDto.setTransactionType(TransactionType.DEPOSIT);
        transactionDto.setCustomer(customerDto);
        transactionDto.setId(1L);
        transactionDto.setStatus(Boolean.TRUE);



       requestAccountDto.setCustomerID(1L);
       requestAccountDto.setInitialCredit(500.0);
    }

    @Test
    void addSubAccount_CustomerExistsAndBalanceEnough() {
        when(customerService.getCustomer(1l)).thenReturn(customerDto);
        when(transactionService.buildTransactionObject(500.0,customerDto,subAccountDto)).thenReturn(transactionDto);
        when(transactionService.addTransaction(transactionDto)).thenReturn(transactionDto);
       SubAccountDto subAccountDtoResult= subAccountService.addSubAccount(requestAccountDto);
        assertNotNull(subAccountDtoResult);
        assertEquals(customerDto,subAccountDtoResult.getCustomer());
        assertEquals(requestAccountDto.getInitialCredit(),subAccountDtoResult.getBalance());


    }
}