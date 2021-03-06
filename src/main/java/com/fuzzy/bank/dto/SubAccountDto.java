package com.fuzzy.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Class representing subAccount to return in case of success creation")
public class SubAccountDto {
    @EqualsAndHashCode.Include
    @ApiModelProperty(name = "id",required = true,dataType = "java.lang.Long")
    private Long id;
    @ApiModelProperty(name = "balance",required = true,dataType = "java.lang.Double")
    private Double balance;
    @ApiModelProperty(name = "parent",required = true,dataType = "com.blueharvest.bank.dto.CustomerDto")
    private CustomerDto customer;

}
