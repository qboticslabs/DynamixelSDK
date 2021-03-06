/*******************************************************************************
* Copyright (c) 2016, ROBOTIS CO., LTD.
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
*
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
*
* * Neither the name of ROBOTIS nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/

/* Author: Ryu Woon Jung (Leon) */

//
// *********     Protocol Combined Example      *********
//
//
// Available Dynamixel model on this example : All models using Protocol 1.0 and 2.0
// This example is tested with a Dynamixel MX-28, a Dynamixel PRO 54-200 and an USB2DYNAMIXEL
// Be sure that properties of Dynamixel MX and PRO are already set as %% MX - ID : 1 / Baudnum : 1 (Baudrate : 1000000) , PRO - ID : 1 / Baudnum : 3 (Baudrate : 1000000)
//

// Be aware that:
// This example configures two different control tables (especially, if it uses Dynamixel and Dynamixel PRO). It may modify critical Dynamixel parameter on the control table, if Dynamixels have wrong ID.
//

import java.util.Scanner;

public class ProtocolCombined
{
  public static void main(String[] args)
  {
    // Control table address for Dynamixel MX
    short ADDR_MX_TORQUE_ENABLE        = 24;                   // Control table address is different in Dynamixel model
    short ADDR_MX_GOAL_POSITION        = 30;
    short ADDR_MX_PRESENT_POSITION     = 36;

    // Control table address for Dynamixel PRO
    short ADDR_PRO_TORQUE_ENABLE        = 562;
    short ADDR_PRO_GOAL_POSITION        = 596;
    short ADDR_PRO_PRESENT_POSITION     = 611;

    // Protocol version
    int PROTOCOL_VERSION1               = 1;                   // See which protocol version is used in the Dynamixel
    int PROTOCOL_VERSION2               = 2;

    // Default setting
    byte DXL1_ID                        = 1;                   // Dynamixel ID: 1
    byte DXL2_ID                        = 2;                   // Dynamixel ID: 2
    int BAUDRATE                        = 1000000;
    String DEVICENAME                   = "/dev/ttyUSB0";      // Check which port is being used on your controller
                                                               // ex) "COM1"   Linux: "/dev/ttyUSB0"

    byte TORQUE_ENABLE                  = 1;                   // Value for enabling the torque
    byte TORQUE_DISABLE                 = 0;                   // Value for disabling the torque
    short DXL1_MINIMUM_POSITION_VALUE  = 100;                  // Dynamixel will rotate between this value
    short DXL1_MAXIMUM_POSITION_VALUE  = 4000;                 // and this value (note that the Dynamixel would not move when the position value is out of movable range. Check e-manual about the range of the Dynamixel you use.)
    int DXL2_MINIMUM_POSITION_VALUE    = -150000;
    int DXL2_MAXIMUM_POSITION_VALUE    = 150000;
    int DXL1_MOVING_STATUS_THRESHOLD   = 10;                   // Dynamixel MX moving status threshold
    int DXL2_MOVING_STATUS_THRESHOLD   = 20;                   // Dynamixel PRO moving status threshold


    String KEY_FOR_ESCAPE               = "e";                 // Key for escape

    int COMM_SUCCESS                    = 0;                   // Communication Success result value
    int COMM_TX_FAIL                    = -1001;               // Communication Tx Failed

    // Instead of getch
    Scanner scanner = new Scanner(System.in);

    // Initialize Dynamixel class for java
    Dynamixel dynamixel = new Dynamixel();

    // Initialize PortHandler Structs
    // Set the port path
    // Get methods and members of PortHandlerLinux or PortHandlerWindows
    int port_num = dynamixel.portHandler(DEVICENAME);

    // Initialize PacketHandler Structs
    dynamixel.packetHandler();

    int index = 0;
    int dxl_comm_result = COMM_TX_FAIL;                         // Communication result
    short[] dxl1_goal_position = new short[]{DXL1_MINIMUM_POSITION_VALUE, DXL1_MAXIMUM_POSITION_VALUE};         // Goal position of Dynamixel MX
    int[] dxl2_goal_position = new int[]{DXL2_MINIMUM_POSITION_VALUE, DXL2_MAXIMUM_POSITION_VALUE};             // Goal position of Dynamixel PRO

    byte dxl_error = 0;                                         // Dynamixel error
    short dxl1_present_position = 0;                            // Present position
    int dxl2_present_position = 0;

    // Open port
    if (dynamixel.openPort(port_num))
    {
      System.out.println("Succeeded to open the port!");
    }
    else
    {
      System.out.println("Failed to open the port!");
      System.out.println("Press any key to terminate...");
      scanner.nextLine();
      return;
    }

    // Set port baudrate
    if (dynamixel.setBaudRate(port_num, BAUDRATE))
    {
      System.out.println("Succeeded to change the baudrate!");
    }
    else
    {
      System.out.println("Failed to change the baudrate!");
      System.out.println("Press any key to terminate...");
      scanner.nextLine();
      return;
    }

    // Enable Dynamixel#1 Torque
    dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION1, DXL1_ID, ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE);
    if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION1)) != COMM_SUCCESS)
    {
      dynamixel.printTxRxResult(PROTOCOL_VERSION1, dxl_comm_result);
    }
    else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION1)) != 0)
    {
      dynamixel.printRxPacketError(PROTOCOL_VERSION1, dxl_error);
    }
    else
    {
      System.out.printf("Dynamixel#%d has been successfully connected\n", DXL1_ID);
    }

    // Enable Dynamixel#2 Torque
    dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION2, DXL2_ID, ADDR_PRO_TORQUE_ENABLE, TORQUE_ENABLE);
    if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION2)) != COMM_SUCCESS)
    {
      dynamixel.printTxRxResult(PROTOCOL_VERSION2, dxl_comm_result);
    }
    else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION2)) != 0)
    {
      dynamixel.printRxPacketError(PROTOCOL_VERSION2, dxl_error);
    }
    else
    {
      System.out.printf("Dynamixel#%d has been successfully connected\n", DXL2_ID);
    }

    while (true)
    {
      System.out.println("Press enter to continue! (or press e then enter to quit!)");
      if(scanner.nextLine().equals(KEY_FOR_ESCAPE))
        break;

      // Write Dynamixel#1 goal position
      dynamixel.write2ByteTxRx(port_num, PROTOCOL_VERSION1, DXL1_ID, ADDR_MX_GOAL_POSITION, dxl1_goal_position[index]);
      if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION1)) != COMM_SUCCESS)
      {
        dynamixel.printTxRxResult(PROTOCOL_VERSION1, dxl_comm_result);
      }
      else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION1)) != 0)
      {
        dynamixel.printRxPacketError(PROTOCOL_VERSION1, dxl_error);
      }

      // Write Dynamixel#2 goal position
      dynamixel.write4ByteTxRx(port_num, PROTOCOL_VERSION2, DXL2_ID, ADDR_PRO_GOAL_POSITION, dxl2_goal_position[index]);
      if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION2)) != COMM_SUCCESS)
      {
        dynamixel.printTxRxResult(PROTOCOL_VERSION2, dxl_comm_result);
      }
      else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION2)) != 0)
      {
        dynamixel.printRxPacketError(PROTOCOL_VERSION2, dxl_error);
      }

      do
      {
        // Read Dynamixel#1 present position
        dxl1_present_position = dynamixel.read2ByteTxRx(port_num, PROTOCOL_VERSION1, DXL1_ID, ADDR_MX_PRESENT_POSITION);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION1)) != COMM_SUCCESS)
        {
          dynamixel.printTxRxResult(PROTOCOL_VERSION1, dxl_comm_result);
        }
        else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION1)) != 0)
        {
          dynamixel.printRxPacketError(PROTOCOL_VERSION1, dxl_error);
        }

        // Read Dynamixel#2 present position
        dxl2_present_position = dynamixel.read4ByteTxRx(port_num, PROTOCOL_VERSION2, DXL2_ID, ADDR_PRO_PRESENT_POSITION);
        if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION2)) != COMM_SUCCESS)
        {
          dynamixel.printTxRxResult(PROTOCOL_VERSION2, dxl_comm_result);
        }
        else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION2)) != 0)
        {
          dynamixel.printRxPacketError(PROTOCOL_VERSION2, dxl_error);
        }

        System.out.printf("[ID: %d] GoalPos: %d  PresPos: %d [ID: %d] GoalPos: %d  PresPos: %d\n", DXL1_ID, dxl1_goal_position[index], dxl1_present_position, DXL2_ID, dxl2_goal_position[index], dxl2_present_position);

      } while ((Math.abs(dxl1_goal_position[index] - dxl1_present_position) > DXL1_MOVING_STATUS_THRESHOLD) || (Math.abs(dxl2_goal_position[index] - dxl2_present_position) > DXL2_MOVING_STATUS_THRESHOLD));

      // Change goal position
      if (index == 0)
      {
        index = 1;
      }
      else
      {
        index = 0;
      }
    }

    // Disable Dynamixel#1 Torque
    dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION1, DXL1_ID, ADDR_MX_TORQUE_ENABLE, TORQUE_DISABLE);
    if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION1)) != COMM_SUCCESS)
    {
      dynamixel.printTxRxResult(PROTOCOL_VERSION1, dxl_comm_result);
    }
    else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION1)) != 0)
    {
      dynamixel.printRxPacketError(PROTOCOL_VERSION1, dxl_error);
    }

    // Disable Dynamixel#2 Torque
    dynamixel.write1ByteTxRx(port_num, PROTOCOL_VERSION2, DXL2_ID, ADDR_PRO_TORQUE_ENABLE, TORQUE_DISABLE);
    if ((dxl_comm_result = dynamixel.getLastTxRxResult(port_num, PROTOCOL_VERSION2)) != COMM_SUCCESS)
    {
      dynamixel.printTxRxResult(PROTOCOL_VERSION2, dxl_comm_result);
    }
    else if ((dxl_error = dynamixel.getLastRxPacketError(port_num, PROTOCOL_VERSION2)) != 0)
    {
      dynamixel.printRxPacketError(PROTOCOL_VERSION2, dxl_error);
    }

    // Close port
    dynamixel.closePort(port_num);

    return;
  }
}
