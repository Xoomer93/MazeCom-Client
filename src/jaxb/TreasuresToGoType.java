//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.20 at 01:38:13 PM CEST 
//


package jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TreasuresToGoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TreasuresToGoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="player" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="treasures" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TreasuresToGoType", propOrder = {
    "player",
    "treasures"
})
public class TreasuresToGoType {

    protected int player;
    protected int treasures;

    /**
     * Gets the value of the player property.
     * 
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Sets the value of the player property.
     * 
     */
    public void setPlayer(int value) {
        this.player = value;
    }

    /**
     * Gets the value of the treasures property.
     * 
     */
    public int getTreasures() {
        return treasures;
    }

    /**
     * Sets the value of the treasures property.
     * 
     */
    public void setTreasures(int value) {
        this.treasures = value;
    }

}
