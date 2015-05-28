//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.28 at 03:42:24 PM CEST 
//


package jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AwaitMoveMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AwaitMoveMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="board" type="{}boardType"/>
 *         &lt;element name="treasuresToGo" type="{}TreasuresToGoType" maxOccurs="4"/>
 *         &lt;element name="foundTreasures" type="{}treasureType" maxOccurs="24" minOccurs="0"/>
 *         &lt;element name="treasure" type="{}treasureType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AwaitMoveMessageType", propOrder = {
    "board",
    "treasuresToGo",
    "foundTreasures",
    "treasure"
})
public class AwaitMoveMessageType {

    @XmlElement(required = true)
    protected BoardType board;
    @XmlElement(required = true)
    protected List<TreasuresToGoType> treasuresToGo;
    protected List<TreasureType> foundTreasures;
    @XmlElement(required = true)
    protected TreasureType treasure;

    /**
     * Gets the value of the board property.
     * 
     * @return
     *     possible object is
     *     {@link BoardType }
     *     
     */
    public BoardType getBoard() {
        return board;
    }

    /**
     * Sets the value of the board property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoardType }
     *     
     */
    public void setBoard(BoardType value) {
        this.board = value;
    }

    /**
     * Gets the value of the treasuresToGo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the treasuresToGo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTreasuresToGo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TreasuresToGoType }
     * 
     * 
     */
    public List<TreasuresToGoType> getTreasuresToGo() {
        if (treasuresToGo == null) {
            treasuresToGo = new ArrayList<TreasuresToGoType>();
        }
        return this.treasuresToGo;
    }

    /**
     * Gets the value of the foundTreasures property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the foundTreasures property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFoundTreasures().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TreasureType }
     * 
     * 
     */
    public List<TreasureType> getFoundTreasures() {
        if (foundTreasures == null) {
            foundTreasures = new ArrayList<TreasureType>();
        }
        return this.foundTreasures;
    }

    /**
     * Gets the value of the treasure property.
     * 
     * @return
     *     possible object is
     *     {@link TreasureType }
     *     
     */
    public TreasureType getTreasure() {
        return treasure;
    }

    /**
     * Sets the value of the treasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreasureType }
     *     
     */
    public void setTreasure(TreasureType value) {
        this.treasure = value;
    }

}
