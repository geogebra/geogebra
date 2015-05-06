/**
 *  Copyright 2013 Ryoya KAWAI
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/

var WebMIDIAPIWrapper = function( sysex ){
    this.midiAccess=null;
    this.ports={"in":[], "out":[]};
    this.devices={ };
    this.sysex=sysex;
    this.performanceNow=window.performance.now();
    this.lastStatusByte=null;
};

WebMIDIAPIWrapper.prototype = {
    initMidi: function() {
        navigator.requestMIDIAccess({sysex: this.sysex}).then( this.scb.bind(this), this.ecb.bind(this) );
    },
    scb: function(access) {
        this.midiAccess = access;
        
        if (typeof this.midiAccess.inputs === "function") {
            this.devices.inputs=this.midiAccess.inputs();
            this.devices.outputs=this.midiAccess.outputs();
        } else {
            var inputIterator = this.midiAccess.inputs.values();
            this.devices.inputs = [];
            for (var o = inputIterator.next(); !o.done; o = inputIterator.next()) {
                this.devices.inputs.push(o.value);
            }
            var outputIterator = this.midiAccess.outputs.values();
            this.devices.outputs = [];
            for (var o = outputIterator.next(); !o.done; o = outputIterator.next()) {
                this.devices.outputs.push(o.value);
            }
        }

        this.setMidiInputSelect.bind(this)();
        this.setMidiOutputSelect.bind(this)();
        console.log("[OutputDevices] ", this.devices.outputs, "[InputDevices]", this.devices.inputs);
    },
    ecb: function(msg){
        console.log("[ERROR] " + msg);
    },
    
    setMidiInputSelect: function() {
        console.log("[ERROR] Set EventHandler : setMidiInputSelect");  
    },
    setMidiOutputSelect: function() {
        console.log("[ERROR] Set EventHandler : setMidiOutputSelect");  
    },

    setMidiInputToPort: function(selIdx, portNo, onmidimessage) {
        var portNo=0;
        this.ports.in[portNo]=this.devices.inputs[selIdx];
        this.ports.in[portNo].onmidimessage=onmidimessage.bind(this);
    },
    setMidiOutputToPort: function(selIdx, portNo) {
        var portNo=0;
        this.ports.out[portNo]=this.devices.outputs[selIdx];
        //this.setPitchBendValue(0, -8192, 8192, 0);
        this.setPitchBendValue(0, 0, 16383, 8192); // Apple DLS Synth
    },
    initializePerformanceNow: function() {
        this.performanceNow=window.performance.now();
    },
    _checkTyeof: function(type, obj) {
        var clas = Object.prototype.toString.call(obj).slice(8, -1);
        return obj !== undefined && obj !== null && clas === type;
    },
    _checkPortNo: function(portNo) {
        if(isNaN(portNo)===true) {
            console.log("[ERROR] PortNo is NOT the type of number. ["+ portNo +"]");
            return false;
        }
        return parseInt(portNo ,10);
    },
    sendNoteOn: function(portNo, ch, note, velocity, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0x9" + ch.toString(16), 16);
        if(typeof time!=="number") {
            time=0;
        }
        var msg=[fb, note, velocity];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendNoteOff: function(portNo, ch, note, velocity, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0x8" + ch.toString(16), 16);
        if(typeof time!=="number") {
            time=0;
        }
        var msg=[fb, note, velocity];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendProgramChange: function(portNo, ch, programNo, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var value = value < 0 ? 0 : value > 127 ? 127 : value;
        var fb=parseInt("0xc" + ch.toString(16), 16);
        if(typeof time!=="number") {
            time=0;
        }
        var msg=[fb, programNo];
        this.ports.out[portNo].send(msg, now+time);
    },
    setPitchBendValue: function(portNo, min, max, center) {
        this.ports.out[portNo].pitchBendValue={"min": parseInt(min), "max": parseInt(max), "center": parseInt(center)};
    },
    sendPitchBend: function(portNo, ch, value, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xe" + ch.toString(16), 16);
        var value = value < this.ports.out[portNo].pitchBendValue.min ? this.ports.out[portNo].pitchBendValue.min : value > this.ports.out[portNo].pitchBendValue.max ? this.ports.out[portNo].pitchBendValue.max : value;
        var msb=(~~(value/128));
        var lsb=(value%128);

        var msg=[fb, lsb, msb];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendSustainStatus: function(portNo, ch, status, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xb" + ch.toString(16), 16);
        var msg=[fb, 0x40, 0x00];
        switch(status) {
            case "on":
            msg=[fb, 0x40, 0x7f];
            break;
        }
        this.ports.out[portNo].send(msg, now+time);
    },
    sendModulationValue: function(portNo, ch, value, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xb" + ch.toString(16), 16);
        var value = value < 0 ? 0 : value > 127 ? 127 : value;
        var msg=[fb, 0x01, value];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendAllSoundOff: function(portNo, ch, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xb" + ch.toString(16), 16);
        var msg=[ fb, 0x78, 0x00 ];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendResetAllController: function(portNo, ch, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xb" + ch.toString(16), 16);
        var msg=[ fb, 0x79, 0x00 ];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendAllNoteOff: function(portNo, ch, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        var fb=parseInt("0xb" + ch.toString(16), 16);
        var msg=[ fb, 0x7b, 0x00 ];
        this.ports.out[portNo].send(msg, now+time);
    },
    sendRaw: function(portNo, msg, time) {
        var portNo=this._checkPortNo(portNo);
        if(portNo===false) {
            console.log("[ERROR] @sendNoteOn");
            return;
        }
        var now=this.performanceNow;
        if(this._checkTyeof("array", msg)===true) {
            console.log("[Error] SendRaw : msg must array." + msg);
            return;
        }
        this.ports.out[portNo].send(msg, now+time);
    },
    parseMIDIMessage: function(msg) {
        if(typeof msg!=="object") {
            return;
        }

        var msg16=new Array();
        var event={ };
        var out={ };
        for(var i=0; i<msg.length; i++) {
            msg16.push(msg[i].toString(16));
        }
        var eventTypeByte=parseInt(msg[0], 16);
        if((msg[0] & 0xf0) == 0xf0) {
            // Systen Event
            if(msg[0]==0xf0) {
                event.type="SysEx";
                event.raw=msg;
            } else {
                console.log("Not Supportted Message. ", msg);
            }
            out={
                "type": event.type,
                "data": event.raw,
                "event": event
            };
        } else {
            // Channel Event
            event.type="channel";
            event.raw=msg;
            // Not Supporting Running
            this.lastStatusByte=msg16[0]; // for Running Status
            event.statusNum=msg16[0].replace("0x", "").substr(0,1).toLowerCase();
            event.channel=parseInt((msg16[0].replace("0x", "").substr(1,1)),16);
            switch(event.statusNum) {
              case "8":
                event.subType="noteOff";
                event.noteNumber=msg[1];
                event.velocity=msg[2];
                break;
              case "9":
                event.subType="noteOn";
                event.noteNumber=msg[1];
                event.velocity=msg[2];
                // 0x9x 0xXX 0x00
                if(event.velocity==0) {
                    event.subType="noteOff";
                }
                break;
              case "a":
                event.subType="noteAftertouch";
                event.noteNumber=msg[1];
                event.amount=msg[2];
                break;
              case "b":
                event.subType="controller";
					      event.ctrlNo = msg[1];
					      event.value = msg[2];
                switch(event.ctrlNo) {
                  case 0x00:
                  case "0x00":
                    event.ctrlName="BankSelect";
                    event.valueType="MSB";
                    break;
                  case 0x20:
                  case "0x20":
                    event.ctrlName="BankSelect";
                    event.valueType="LSB";
                    break;
                  case 0x01:
                  case "0x01":
                    event.ctrlName="Modulation";
                    event.valueType="MSB";
                    break;
                  case 0x21:
                  case "0x21":
                    event.ctrlName="Modulation";
                    event.valueType="LSB";
                    break;
                  case 0x05:
                  case "0x05":
                    event.ctrlName="Portament";
                    event.valueType="MSB";
                    break;
                  case 0x25:
                  case "0x25":
                    event.ctrlName="Portament";
                    event.valueType="LSB";
                    break;
                  case 0x06:
                  case "0x06":
                    event.ctrlName="DataEntry";
                    event.valueType="MSB";
                    break;
                  case 0x26:
                  case "0x26":
                    event.ctrlName="DataEntry";
                    event.valueType="LSB";
                    break;
                  case 0x07:
                  case "0x07":
                    event.ctrlName="MainVolume";
                    event.valueType="MSB";
                    break;
                  case 0x27:
                  case "0x27":
                    event.ctrlName="MainVolume";
                    event.valueType="LSB";
                    break;
                  case 0x10:
                  case "0x10":
                    event.ctrlName="PanPot";
                    event.valueType="MSB";
                    break;
                  case 0x2a:
                  case "0x2a":
                    event.ctrlName="PanPot";
                    event.valueType="LSB";
                    break;
                  case 0x11:
                  case "0x11":
                    event.ctrlName="Expression";
                    event.valueType="MSB";
                    break;
                  case 0x2b:
                  case "0x2b":
                    event.ctrlName="Expression";
                    event.valueType="LSB";
                    break;
                  case 0x40:
                  case "0x40":
                    event.ctrlName="Hold";
                    event.ctrlStatus="Off";
                    if(event.value>=0x40) {
                        event.ctrlStatus="On";
                    }
                    break;
                  case 0x41:
                  case "0x41":
                    event.ctrlName="Portament";
                    event.ctrlStatus="Off";
                    if(event.value>=0x40) {
                        event.ctrlStatus="On";
                    }
                    break;
                  case 0x42:
                  case "0x42":
                    event.ctrlName="SosTenuto";
                    event.ctrlStatus="Off";
                    if(event.value>=0x40) {
                        event.ctrlStatus="On";
                    }
                    break;
                  case 0x43:
                  case "0x43":
                    event.ctrlName="SoftPedal";
                    event.ctrlStatus="Off";
                    if(event.value>=0x40) {
                        event.ctrlStatus="On";
                    }
                    break;
                  case 0x46:
                  case "0x46":
                    event.ctrlName="SoundController1";
                    break;
                  case 0x47:
                  case "0x47":
                    event.ctrlName="SoundController2";
                    break;
                  case 0x48:
                  case "0x48":
                    event.ctrlName="SoundController3";
                    break;
                  case 0x49:
                  case "0x49":
                    event.ctrlName="SoundController4";
                    break;
                  case 0x50:
                  case "0x50":
                    event.ctrlName="SoundController5";
                    break;
                  case 0x5b:
                  case "0x5b":
                    event.ctrlName="effectSendLevel1"; // SendLevel: Reberb 
                    break;
                  case 0x5d:
                  case "0x5d":
                    event.ctrlName="effectSendLevel3"; // SendLevel: Chrus 
                    break;
                  case 0x5e:
                  case "0x5e":
                    event.ctrlName="effectSendLevel4"; // [XG] ValiationEffect, [SC-88] SendLevel: Delay
                    break;
                  case 0x60:
                  case "0x60":
                    event.ctrlName="DataIncrement";
                    break;
                  case 0x61:
                  case "0x61":
                    event.ctrlName="DataDecrement";
                    break;
                  case 0x62:
                  case "0x62":
                    event.ctrlName="NRPN";
                    event.valueType="LSB";
                    break;
                  case 0x63:
                  case "0x63":
                    event.ctrlName="NRPN";
                    event.valueType="MSB";
                    break;
                  case 0x64:
                  case "0x64":
                    event.ctrlName="RPN";
                    event.valueType="LSB";
                    break;
                  case 0x65:
                  case "0x65":
                    event.ctrlName="RPN";
                    event.valueType="MSB";
                    break;
                  case 0x78:
                  case "0x78":
                    event.ctrlName="AllSoundOff";
                    break;
                  case 0x79:
                  case "0x79":
                    event.ctrlName="ResetAllController";
                    break;
                  case 0x7b:
                  case "0x7b":
                    event.ctrlName="OmniOff";
                    break;
                  case 0x7c:
                  case "0x7c":
                    event.ctrlName="OmniOn";
                    break;
                  case 0x7e:
                  case "0x7e":
                    event.ctrlName="Mono";
                    break;
                  case 0x7f: 
                  case "0x7f":
                    event.ctrlName="Poly";
                    break;
                default:
                    event.ctrlName="NotDefined";
                    break;
                }
                
                break;
              case "c":
          	    event.subType = 'programChange';
					      event.programNumber = msg[1];
                break;
				      case "d":
					      event.subType = 'channelAftertouch';
					      event.amount = msg[1];
                break;
				      case "e":
					      event.subType = 'pitchBend';
                var msb=msg[2], lsb=msg[1];
                if( (msg[2]>>6).toString(2)=="1" ) {
                    event.value = -1*(((msb-64)<<7) + lsb +1) ;
                } else {
                    var bsMsb=msb<<7;
					          event.value = bsMsb + lsb;
                }
                break;
            default:
                console.log("Not Supportted Message. ", msg);
                return;
                break;
            }
            out={
                "type": event.type,
                "subType": event.subType,
                "data" : event.raw,
                "event": event
            };
        }
        return out;
    }
};

