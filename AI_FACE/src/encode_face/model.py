import torch
import torch.nn as nn
import torch.nn.functional as F

class BasicBlock(nn.Module):
    # Khai báo hệ số mở rộng (expansion factor). Đối với Basic Block, nó là 1.
    expansion = 1

    def __init__(self, in_channels, out_channels, stride=1):
        super(BasicBlock, self).__init__()

        # Lớp Tích chập thứ nhất: 3x3, có padding, sử dụng stride
        self.conv1 = nn.Conv2d(
            in_channels,
            out_channels,
            kernel_size=3,
            stride=stride,
            padding=1,
            bias=False
        )
        self.bn1 = nn.BatchNorm2d(out_channels)  # Chuẩn hóa theo lô (Batch Normalization)

        # Lớp Tích chập thứ hai: 3x3, stride luôn là 1 sau khi đã xử lý stride ở conv1
        self.conv2 = nn.Conv2d(
            out_channels,
            out_channels,
            kernel_size=3,
            stride=1,
            padding=1,
            bias=False
        )
        self.bn2 = nn.BatchNorm2d(out_channels)

        # Định nghĩa kết nối tắt (shortcut connection)
        self.shortcut = nn.Sequential()
        # Nếu stride khác 1 hoặc số kênh đầu vào khác đầu ra,
        # cần một lớp 1x1 conv để khớp kích thước và số kênh cho phép cộng.
        if stride != 1 or in_channels != self.expansion * out_channels:
            self.shortcut = nn.Sequential(
                nn.Conv2d(
                    in_channels,
                    self.expansion * out_channels,
                    kernel_size=1,
                    stride=stride,
                    bias=False
                ),
                nn.BatchNorm2d(self.expansion * out_channels)
            )

    def forward(self, x):
        # Lưu lại input để dùng cho skip connection
        out = F.relu(self.bn1(self.conv1(x)))
        out = self.bn2(self.conv2(out))

        # Thêm kết nối tắt vào output chính
        out += self.shortcut(x)
        out = F.relu(out)  # Áp dụng ReLU sau khi cộng
        return out

# --- Mô hình ResNet Hoàn chỉnh ---
class ResNet(nn.Module):
    def __init__(self, block, num_blocks, num_classes=10):
        super(ResNet, self).__init__()
        self.in_channels = 64  # Số kênh đầu vào ban đầu cho stage 1

        # 1. Lớp Tích chập Ban đầu: Thường 7x7 hoặc 3x3. Ở đây dùng 3x3 cho bộ dữ liệu nhỏ hơn (ví dụ: CIFAR)
        self.conv1 = nn.Conv2d(
            3,  # Đầu vào 3 kênh (RGB)
            64,
            kernel_size=3,
            stride=1,
            padding=1,
            bias=False
        )
        self.bn1 = nn.BatchNorm2d(64)

        # (Lưu ý: Đối với ImageNet, thường có thêm Max Pooling 3x3 sau conv1)

        # 2. Xây dựng các tầng (layers) ResNet
        # Các tầng này được xây dựng từ các khối kiến tạo (BasicBlock)
        self.layer1 = self._make_layer(block, 64, num_blocks[0], stride=1)
        self.layer2 = self._make_layer(block, 128, num_blocks[1], stride=2)  # Giảm kích thước không gian (Downsampling)
        self.layer3 = self._make_layer(block, 256, num_blocks[2], stride=2)  # Giảm kích thước không gian
        self.layer4 = self._make_layer(block, 512, num_blocks[3], stride=2)  # Giảm kích thước không gian

        # 3. Lớp Tổng hợp (Pooling) và Lớp Fully Connected (FC)
        self.linear = nn.Linear(512 * block.expansion, num_classes)  # FC layer cho đầu ra phân loại

    # Hàm trợ giúp để tạo ra một tầng gồm nhiều Basic Block
    def _make_layer(self, block, out_channels, num_blocks, stride):
        # Stride cho Basic Block đầu tiên trong tầng để downsample
        strides = [stride] + [1] * (num_blocks - 1)
        layers = []

        for stride in strides:
            layers.append(block(self.in_channels, out_channels, stride))
            # Cập nhật số kênh đầu vào cho Basic Block tiếp theo
            self.in_channels = out_channels * block.expansion

        return nn.Sequential(*layers)

    def forward(self, x):
        # Tầng đầu tiên
        out = F.relu(self.bn1(self.conv1(x)))
        # (Đối với ImageNet, thêm F.max_pool2d ở đây)

        # Các tầng ResNet chính
        out = self.layer1(out)
        out = self.layer2(out)
        out = self.layer3(out)
        out = self.layer4(out)

        # Tổng hợp thích nghi trung bình (Adaptive Average Pooling) để thu nhỏ xuống 1x1
        out = F.adaptive_avg_pool2d(out, (1, 1))

        # Làm phẳng (Flatten) và đi qua lớp Fully Connected
        out = out.view(out.size(0), -1)
        out = self.linear(out)
        return out


# --- Khởi tạo Model ResNet-18 ---
def ResNet18(num_classes=10):
    # ResNet-18 có cấu trúc số lượng khối Basic Block là [2, 2, 2, 2]
    return ResNet(BasicBlock, [2, 2, 2, 2], num_classes)


# --- Ví dụ sử dụng ---
# Khởi tạo mô hình
net = ResNet18(num_classes=10)

# Tạo một tensor input giả: lô 1 ảnh, 3 kênh (RGB), kích thước 32x32 (ví dụ như CIFAR)
dummy_input = torch.randn(1, 3, 32, 32)

# Chạy mô hình để xem output
output = net(dummy_input)

# In kích thước đầu ra
print(f"Kích thước đầu vào: {dummy_input.shape}")
print(f"Kích thước đầu ra: {output.shape} (Lô 1, 10 lớp phân loại)")