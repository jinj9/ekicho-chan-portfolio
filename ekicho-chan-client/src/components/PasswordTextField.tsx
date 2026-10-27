import React from 'react';
import { IconButton, InputAdornment, TextField, TextFieldProps } from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';

const PasswordTextField: React.FC<TextFieldProps> = (props) => {
  const [visible, setVisible] = React.useState(false);

  return (
    <TextField
      {...props}
      type={visible ? 'text' : 'password'}
      InputProps={{
        ...props.InputProps,
        endAdornment: (
          <InputAdornment position="end">
            <IconButton
              aria-label={visible ? 'パスワードを非表示にする' : 'パスワードを表示する'}
              edge="end"
              onClick={() => setVisible(current => !current)}
              onMouseDown={event => event.preventDefault()}
            >
              {visible ? <VisibilityOff /> : <Visibility />}
            </IconButton>
          </InputAdornment>
        ),
      }}
    />
  );
};

export default PasswordTextField;
