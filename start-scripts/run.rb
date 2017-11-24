require 'libusb'

`./scripts/init.sh`

def get_dev_path(device_identifier)
  matching_output = `./scripts/list_devices.sh`.split(/\n/).select{ |i| i[device_identifier] }[0]
  if matching_output
    return matching_output.split(' ')[0]
  end
  nil
end

usb = LIBUSB::Context.new

deviceIds = {
  knock_sensor_1: {idVendor: 9025, idProduct: 32833, serial: ""},
  rfid_reader_1: {idVendor: 1027, idProduct: 24577, serial: "AL01EY4M"},

  knock_sensor_2: {idVendor: 6790, idProduct: 29987, serial: ""},
  rfid_reader_2: {idVendor: 1027, idProduct: 24577, serial: "AL01EXHK"}
}

deviceIds.each() do |k, v|
  device = usb.devices(idVendor: v[:idVendor], idProduct: v[:idProduct]).first
  if device
    model_id = v[:idProduct].to_s(16).rjust(4, "0")
    vendor_id = v[:idVendor].to_s(16)
    serial_number = v[:serial]
    dev_path = get_dev_path("#{vendor_id} - #{serial_number} - #{model_id}")
    if dev_path
      puts "#{k.to_s} : #{dev_path}"
    end
  end
end
