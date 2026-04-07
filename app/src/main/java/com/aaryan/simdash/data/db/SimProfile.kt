package com.aaryan.simdash.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sim_profiles")
data class SimProfile(
    @PrimaryKey
    val subscriptionId: Int,             
    val simSlotIndex: Int,               
    val carrierName: String,             
    val displayName: String,             
    val mccMnc: String,                  
    val ussdCode: String,                
    val planType: String,                
    val dailyLimitBytes: Long,           
    val totalPoolBytes: Long,            
    val validityEndDate: Long,           
    val lastUssdFetchAt: Long,           
    val todayUsedBytes: Long,            
    val lastUpdatedAt: Long,             
    val canSeparateSims: Boolean,        
    val morningAlertEnabled: Boolean,    
    val morningAlertTimeHour: Int,       
    val morningAlertTimeMinute: Int,     
    val validityAlertEnabled: Boolean,   
    val widgetTheme: String              
)
