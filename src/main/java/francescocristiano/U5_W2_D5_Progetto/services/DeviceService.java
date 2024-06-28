package francescocristiano.U5_W2_D5_Progetto.services;

import francescocristiano.U5_W2_D5_Progetto.entities.Device;
import francescocristiano.U5_W2_D5_Progetto.entities.Employee;
import francescocristiano.U5_W2_D5_Progetto.enums.DeviceStatus;
import francescocristiano.U5_W2_D5_Progetto.enums.DeviceType;
import francescocristiano.U5_W2_D5_Progetto.exceptions.BadRequestException;
import francescocristiano.U5_W2_D5_Progetto.exceptions.NotFoundException;
import francescocristiano.U5_W2_D5_Progetto.payloads.NewAssignmentDTO;
import francescocristiano.U5_W2_D5_Progetto.payloads.NewDeviceDTO;
import francescocristiano.U5_W2_D5_Progetto.payloads.NewUpdateDeviceDTO;
import francescocristiano.U5_W2_D5_Progetto.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EmployeeService employeeService;

    public Device saveDevice(NewDeviceDTO devicePayload) {
      /*  Employee employee = null;
        if (devicePayload.employeeId() != null) {
            employee = employeeService.findEmployeeById(devicePayload.employeeId());
        }*/
        Device newDevice = new Device(DeviceType.getDeviceType(devicePayload.deviceType()), DeviceStatus.ONLINE, null);
        return deviceRepository.save(newDevice);
    }

    public Device findDeviceById(UUID id) {
        return deviceRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }


    public Page<Device> findAllDevices(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return deviceRepository.findAll(pageable);
    }

    public Device findDeviceByIdAndUpdate(UUID id, NewUpdateDeviceDTO updatedDevicePayload) {
        Device foundDevice = findDeviceById(id);
        if (DeviceStatus.getDeviceStatus(updatedDevicePayload.deviceStatus()) == DeviceStatus.ASSIGNED && foundDevice.getEmployee() == null) {
            throw new BadRequestException("Device is not assigned to an employee, cannot update device status with assigned status before assigning it to an employee");
        }
        Device updatedDevice = new Device(DeviceType.getDeviceType(updatedDevicePayload.deviceType()), DeviceStatus.getDeviceStatus(updatedDevicePayload.deviceStatus()), foundDevice.getEmployee());
        foundDevice.setDeviceType(updatedDevice.getDeviceType());
        foundDevice.setDeviceStatus(updatedDevice.getDeviceStatus());
        return deviceRepository.save(foundDevice);
    }

    public void deleteDeviceById(UUID id) {
        deviceRepository.deleteById(id);
    }

    public Device assignDeviceToEmployee(UUID id, NewAssignmentDTO employeeIdPayload) {
        Device device = findDeviceById(id);
        if (employeeIdPayload.employeeId() == null) {
            device.setEmployee(null);
            device.setDeviceStatus(DeviceStatus.ONLINE);
            return deviceRepository.save(device);
        }
        Employee employee = employeeService.findEmployeeById(employeeIdPayload.employeeId());
       /* if (device.getDeviceStatus() == DeviceStatus.ASSIGNED) {
            throw new BadRequestException("Device is already assigned to an employee");
        }

        Ho preferito non usare questo controllo che avevo implementato all'inizio perchè in questo modo
        avrebbe reso il dipendente collegato ad un dispositivo assegnato immutabile, mentre
        io volevo qualcosa di più flessibile

        */
        device.setEmployee(employee);
        if (device.getDeviceStatus() != DeviceStatus.ASSIGNED) {
            device.setDeviceStatus(DeviceStatus.ASSIGNED);
        }
        return deviceRepository.save(device);
    }
}
