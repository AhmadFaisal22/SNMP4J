/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmp4j;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


public class SNMP4J {

    /**
     * @param args the command line arguments
     */
    //Menyiapkan variabel global    
    Snmp snmp = null;
    String address = null;
    
    public SNMP4J(String add){
        address = add;
    }
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here\
        //Koneksi ke IP address target        
        SNMP4J client = new SNMP4J("udp:127.0.0.1/161"); //IP address localhost //Port Default SNMP /161
        client.start();
        
        //MIB OID
        String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0")); //OID untuk melihat deskripsi hardware
        System.out.println(sysDescr); //Mencetak hasil response dari agent
    }
    
    //Menyiapkan SNMP session
    private void start() throws IOException{
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }
    
    //Untuk menangani OID dan mengembalikan response dari agent berupa string
    public String getAsString(OID oid) throws IOException{
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toString();
    }
    
    //Untuk menangani beberapa OID
    public ResponseEvent get(OID oids[]) throws IOException{
        PDU pdu = new PDU();
        for(OID oid : oids){
            pdu.add(new VariableBinding(oid));  
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, getTarget(), null);
        if(event != null){
            return event;    
        }
        throw new RuntimeException("Get time out");
    }
    
    //Mengembalikan target yang berisi tentang bagaimana dan dimana data harus diambil
    private Target getTarget(){
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
