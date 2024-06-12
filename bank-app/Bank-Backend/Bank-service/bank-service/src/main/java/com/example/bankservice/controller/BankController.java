package com.example.bankservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.bankservice.dao.EducationLoanDao;
import com.example.bankservice.dao.PersonalLoanDao;
import com.example.bankservice.entity.EducationLoan;
import com.example.bankservice.entity.LoanDetails;
import com.example.bankservice.entity.PersonalLoan;
import com.example.bankservice.entity.User;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
@RestController
@RequestMapping("/api/bank")
public class BankController {
	public static final String BANK_SERVICE="bankService";
	
	@Autowired
	@Lazy
	RestTemplate template;
	
	@GetMapping("/user/{uid}")
	public User getUser(@PathVariable int uid) {
		return template.getForObject("http://user-service:8100/api/user/userDetails/"+uid, User.class);
	}
	
	
	@PostMapping("/addEducationLoan")
	public ResponseEntity<EducationLoan> saveEducationLoan(@RequestBody EducationLoanDao loan){
		
		EducationLoan eduLoan =  template.postForObject("http://user-service:8100/api/user/addEducationLoan",loan, EducationLoan.class);
		if(eduLoan != null)return new ResponseEntity<EducationLoan>(eduLoan,HttpStatus.CREATED );
		else return new ResponseEntity<EducationLoan>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/addPersonalLoan")
	public ResponseEntity<PersonalLoan> savePersonalLoan(@RequestBody PersonalLoanDao loan){
		PersonalLoan personalLoan = template.postForObject("http://user-service:8100/api/user/addPersonalLoan",loan, PersonalLoan.class);
		if(personalLoan != null)return new ResponseEntity<PersonalLoan>(personalLoan,HttpStatus.CREATED );
		else return new ResponseEntity<PersonalLoan>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/getAllLoans/{uid}")
	@Retry(name ="bankService",fallbackMethod = "getEmptyResponse")
	public ResponseEntity<List<LoanDetails>> getLoanDetailsByUser(@PathVariable int uid){
		List<LoanDetails> loans =  template.getForObject("http://user-service:8100/api/user/getLoansByUser/"+uid, List.class);
		return new ResponseEntity<List<LoanDetails>>(loans, HttpStatus.OK);
	}
	
	public ResponseEntity<List<LoanDetails>> getEmptyResponse(){
		List<LoanDetails> loans = new ArrayList<LoanDetails>();
		System.out.println(loans);
		return new ResponseEntity<List<LoanDetails>>(loans, HttpStatus.OK);
	}
	
	@GetMapping("/getLoanDetails/{loanId}")
	public ResponseEntity<Object> getLoanDetailsById(@PathVariable int loanId){
		Object o = template.getForObject("http://user-service:8100/api/user/getLoan/"+loanId, Object.class);
		
		if(o != null)return new ResponseEntity<Object>(o,HttpStatus.CREATED );
		else return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
	}

}
