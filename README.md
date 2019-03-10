# Bluetooth Low Energy Baseband

This Documentation is for the Bluetooth Low Energy (BLE) Baseband. It has been synthesized on both FPGA and ASIC platforms and comes with testbenches.

## Project History
This project is based off work in EE290C @ UC Berkeley 2018 fall semester as well as EE290C Spring 2018. The [list of contributors](https://github.com/tapeout/ble-baseband/graphs/contributors) can be found [here](https://github.com/tapeout/ble-baseband/graphs/contributors).

2018 Fall EE290C, taught by Prof. Borivoje Nikolic, offers Advanced Topics in Circuit Design: VLSI Signal Processing. The course adopts Chisel, an open-source hardware construction language developed at UC Berkeley, to implement digital signal processing designs. The design could be verified by hooked up to the Rocket Chip. Our group chose to implement a BLE baseband for the final project.

## Project Overview
The BLE baseband includes two main blocks: packet assmebler (PA) and disassmbler (PDA), which are responsible for TX and RX sides respectively. Two submodules, CRC and (de)whitening, are attached to PA/PDA to follow Bluetooth Specification v5.0. The final goal is to implement a complete BLE baseband loop chain. 

The diagram of the expected BLE loop chain is shown below:

![blockDiagram](doc/image/loopback_chain.png)
<br>

## Major Contributors
* 2018 Fall EE290C: Jerry Duan, Mingying Xie, and Yalun Zheng
* 2018 Spring EE290C: Richard Renn, Charles Huang
<br><br>

## Modules
1) PA: 
[packet assembler](doc/pa.md), 
[PA chain](doc/pa_chain.md)
2) PDA: 
[packet disassembler](doc/pda.md), 
[PDA chain](doc/pda_chain.md)
3) CRC: 
[CRC](doc/crc.md)
4) Whitening: 
[Whitening](doc/whitening.md)
5) Top level: 
[loopback](doc/loop.md)
<br>

## Working Principle
Below is an overall diagram of the BLE packet structure. As specified in Bluetooth Specification v5.0, the BLE packet includes Preamble, Access Address, PDU (header and payload), and CRC.

![blockDiagram](doc/image/ble_packet_detail.png)

### Preamble
For LE 1M packet, the preamble is determined by the last bit of access address (AA). It should be noted here, since the AA sequence has to be flipped for transmitting, the bit we actually look at is the first bit in the AA field (if 0, preamble is 01010101; if 1, preamble is 10101010).

![blockDiagram](doc/image/preamble.png)

### Access Address
The standard AA for broadcasting is 0x8E89BED6. Thus the encoded AA is 0110_1011_0111_1101_1001_0001_0111_0001.
Matlab code demonstration: ``fliplr(dec2bin(hex2dec('8E89BED6'),32))``

### PDU: Header
The PDU header contains 6 parts: PDU type, RFU, RxAdd, TxAdd, ChSel and Length.
![blockDiagram](doc/image/pdu_header.png)
#### PDU type
Our project adopts `ADV_NONCONN_IND` in LE 1M packet implementaion.
![blockDiagram](doc/image/pdu_type.png)

#### RFU
Reserved for future use in Spec v.05.
#### RxAdd, TxAdd
0 means public address, 1 means randomized address. Set both to 0 in this project. 
#### Length
Indicate the size of payload in bytes/octets. The length should be larger than 6 (reserved for advertising address) and less than 37. For example, if we have 6 for address, 3 for headers, 6 for payload, then PDU_length is 15. Similar to AA, the transmition start with LSB, so length 15 is 11110000 to keep the correct order.


### PDU: Payload
![blockDiagram](doc/image/payload.png)

There are three sections within the payload we used, which are advertising address and two advertising data (denoted as payload1 and payload2).
#### Advertising Address
The advertising address field contains the device address of the advertiser (BLE Spec Vol 6, Part B, Section 2.3.4.1). In this project, AdvA is set to the MAC address of the advertiser. This should also follow LSB-first transmission order.
#### Advertising Data
The advertising data should contain 0 or more sections. Each section is consisted of one byte indicating the length of the section, one byte indicating the GAP code (refer to [GAP Code definition](https://www.bluetooth.com/specifications/assigned-numbers/generic-access-profile)), and one byte indicating the data.
For example, in the current packet, the payload1 is 0x02 (length), 0x01 (“flags”), 0x05 (flag data). The payload2 is 0x05 (length), type 0x08 (“short name”), data 0x32 0x39 0x30 0x43(ASCII code for “290C”).

### CRC
For this part, please refer to [CRC](doc/crc.md) and [whitening](doc/whitening.md).


### Noted:
BLE Spec has an explanation about whether we have to reverse the bit sequence: "Multi-octet fields, with the exception of the Cyclic Redundancy Check (CRC) and the Message Integrity Check (MIC), shall be transmitted with the least significant octet first. Each octet within multi-octet fields, with the exception of the CRC (see Section 3.1.1), shall be transmitted in LSB first order. For example, the 48-bit addresses in the advertising channel PDUs shall be transmitted with the least significant octet first, followed by the remainder of the five octets in increasing order."


## TODO
- Add-on features like FEC mentioned in Bluetooth 5 Spec

- Besides advertising PDU type, implement scan type (eg.`SCAN_REQ`), connect (`CONNECT_REQ`) and so on

- Apply more random-generated test cases

- Take operation frequency into considerations when the digital BLE baseband has to talk with analog circuits
<br>

## Acknowledgement
Here is our appreciation to Prof. Borivoje Nikolic, Prof. Kristofer Pister and the GSI Paul Rigge for guiding us in this project. Their valuable suggestions and feedback help us move forward. Also the work from last semester's group inspired us greatly and here is their tape-out work has been integrated into this repo. Lastly, we would like to thank David Burnett and Rachel Zoll for helping us get on board and explain BLE packet structure and tests.
