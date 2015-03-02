/*    */ package com.wily.fieldext.epaplugins.utils;
/*    */ 
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
/*    */ 
/*    */ class ProxyAuthenticator extends Authenticator
/*    */ {
/*    */   private String user;
/*    */   private String password;
/*    */ 
/*    */   public ProxyAuthenticator(String user, String password)
/*    */   {
/* 11 */     this.user = user;
/* 12 */     this.password = password;
/*    */   }
/*    */ 
/*    */   protected PasswordAuthentication getPasswordAuthentication() {
/* 16 */     return new PasswordAuthentication(this.user, this.password.toCharArray());
/*    */   }
/*    */ }

/* Location:           \\vmware-host\Shared Folders\Documents\1APM-Wily\EPA\CloudMonitor\APMCMAgent\lib\APMCMPlugin.jar
 * Qualified Name:     com.wily.fieldext.epaplugins.utils.ProxyAuthenticator
 * JD-Core Version:    0.6.0
 */