//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.28 at 03:42:24 PM CEST 
//


package model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="LoginMessage" type="{}LoginMessageType"/>
 *         &lt;element name="LoginReplyMessage" type="{}LoginReplyMessageType"/>
 *         &lt;element name="AwaitMoveMessage" type="{}AwaitMoveMessageType"/>
 *         &lt;element name="MoveMessage" type="{}MoveMessageType"/>
 *         &lt;element name="AcceptMessage" type="{}AcceptMessageType"/>
 *         &lt;element name="WinMessage" type="{}WinMessageType"/>
 *         &lt;element name="DisconnectMessage" type="{}DisconnectMessageType"/>
 *       &lt;/choice>
 *       &lt;attribute name="mcType" use="required" type="{}MazeComType" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "loginMessage",
    "loginReplyMessage",
    "awaitMoveMessage",
    "moveMessage",
    "acceptMessage",
    "winMessage",
    "disconnectMessage"
})
@XmlRootElement(name = "MazeCom")
public class MazeCom {

    @XmlElement(name = "LoginMessage")
    protected LoginMessageType loginMessage;
    @XmlElement(name = "LoginReplyMessage")
    protected LoginReplyMessageType loginReplyMessage;
    @XmlElement(name = "AwaitMoveMessage")
    protected AwaitMoveMessageType awaitMoveMessage;
    @XmlElement(name = "MoveMessage")
    protected MoveMessageType moveMessage;
    @XmlElement(name = "AcceptMessage")
    protected AcceptMessageType acceptMessage;
    @XmlElement(name = "WinMessage")
    protected WinMessageType winMessage;
    @XmlElement(name = "DisconnectMessage")
    protected DisconnectMessageType disconnectMessage;
    @XmlAttribute(name = "mcType", required = true)
    protected MazeComType mcType;
    @XmlAttribute(name = "id", required = true)
    protected int id;

    /**
     * Gets the value of the loginMessage property.
     * 
     * @return
     *     possible object is
     *     {@link LoginMessageType }
     *     
     */
    public LoginMessageType getLoginMessage() {
        return loginMessage;
    }

    /**
     * Sets the value of the loginMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginMessageType }
     *     
     */
    public void setLoginMessage(LoginMessageType value) {
        this.loginMessage = value;
    }

    /**
     * Gets the value of the loginReplyMessage property.
     * 
     * @return
     *     possible object is
     *     {@link LoginReplyMessageType }
     *     
     */
    public LoginReplyMessageType getLoginReplyMessage() {
        return loginReplyMessage;
    }

    /**
     * Sets the value of the loginReplyMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginReplyMessageType }
     *     
     */
    public void setLoginReplyMessage(LoginReplyMessageType value) {
        this.loginReplyMessage = value;
    }

    /**
     * Gets the value of the awaitMoveMessage property.
     * 
     * @return
     *     possible object is
     *     {@link AwaitMoveMessageType }
     *     
     */
    public AwaitMoveMessageType getAwaitMoveMessage() {
        return awaitMoveMessage;
    }

    /**
     * Sets the value of the awaitMoveMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AwaitMoveMessageType }
     *     
     */
    public void setAwaitMoveMessage(AwaitMoveMessageType value) {
        this.awaitMoveMessage = value;
    }

    /**
     * Gets the value of the moveMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MoveMessageType }
     *     
     */
    public MoveMessageType getMoveMessage() {
        return moveMessage;
    }

    /**
     * Sets the value of the moveMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoveMessageType }
     *     
     */
    public void setMoveMessage(MoveMessageType value) {
        this.moveMessage = value;
    }

    /**
     * Gets the value of the acceptMessage property.
     * 
     * @return
     *     possible object is
     *     {@link AcceptMessageType }
     *     
     */
    public AcceptMessageType getAcceptMessage() {
        return acceptMessage;
    }

    /**
     * Sets the value of the acceptMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptMessageType }
     *     
     */
    public void setAcceptMessage(AcceptMessageType value) {
        this.acceptMessage = value;
    }

    /**
     * Gets the value of the winMessage property.
     * 
     * @return
     *     possible object is
     *     {@link WinMessageType }
     *     
     */
    public WinMessageType getWinMessage() {
        return winMessage;
    }

    /**
     * Sets the value of the winMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link WinMessageType }
     *     
     */
    public void setWinMessage(WinMessageType value) {
        this.winMessage = value;
    }

    /**
     * Gets the value of the disconnectMessage property.
     * 
     * @return
     *     possible object is
     *     {@link DisconnectMessageType }
     *     
     */
    public DisconnectMessageType getDisconnectMessage() {
        return disconnectMessage;
    }

    /**
     * Sets the value of the disconnectMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisconnectMessageType }
     *     
     */
    public void setDisconnectMessage(DisconnectMessageType value) {
        this.disconnectMessage = value;
    }

    /**
     * Gets the value of the mcType property.
     * 
     * @return
     *     possible object is
     *     {@link MazeComType }
     *     
     */
    public MazeComType getMcType() {
        return mcType;
    }

    /**
     * Sets the value of the mcType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MazeComType }
     *     
     */
    public void setMcType(MazeComType value) {
        this.mcType = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

}