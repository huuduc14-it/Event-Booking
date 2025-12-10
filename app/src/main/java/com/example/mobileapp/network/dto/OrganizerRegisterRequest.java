package com.example.mobileapp.network.dto;

public class OrganizerRegisterRequest {
    public String organization_name;
    public String tax_code;
    public String address;
    public String bank_account;
    public String contact_email;
    public String contact_phone;

    public OrganizerRegisterRequest(String organization_name, String tax_code,
                                    String address, String bank_account,
                                    String contact_email, String contact_phone) {
        this.organization_name = organization_name;
        this.tax_code = tax_code;
        this.address = address;
        this.bank_account = bank_account;
        this.contact_email = contact_email;
        this.contact_phone = contact_phone;
    }
}