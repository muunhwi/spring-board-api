package api.board.service;

import api.board.exception.FileImageException;
import api.board.object.board.BoardImage;
import api.board.object.dto.Image.ImageFile;
import api.board.repository.BoardImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploaderService {

    @Value("${file.directory}")
    private String location;
    private final BoardImageRepository boardImageRepository;

    public ImageFile imageUploader(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(index);
        String uuid = UUID.randomUUID().toString().substring(0,10);

        BoardImage image = BoardImage.builder()
                .originalName(originalFilename)
                .serverSavedName(uuid + extension)
                .build();

        BoardImage save = boardImageRepository.save(image);

        try {
            file.transferTo(Paths.get(location + image.getServerSavedName()));
        } catch (IOException e) {
            throw new FileImageException("Image");
        }

        return new ImageFile(save.getId(), "/board/image/" + save.getId());
    }

    public byte[] getImage(Long id)  {
        Optional<BoardImage> findImage = boardImageRepository.findById(id);
        BoardImage boardImage = findImage.orElseThrow(EntityNotFoundException::new);
        try {
            InputStream in = new FileInputStream(location + boardImage.getServerSavedName());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch(IOException e) {
            throw new FileImageException("Image");
        }
    }





}
