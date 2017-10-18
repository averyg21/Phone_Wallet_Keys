package com.example.averygrimes.phone_wallet_keys;

import java.io.Serializable;

public class DeviceModel implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String status;
    
    public DeviceModel()
    {

    }
    
    public DeviceModel(String name, String status)
    {
        this.name = name;
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
