import React from 'react';
import { Grid, Paper, Typography } from '@mui/material';
import { useParams } from 'react-router-dom';

const ReviewDetailPage = () => {
  const { id } = useParams();

  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h5" gutterBottom>
            리뷰 상세 정보
          </Typography>
          <Typography>
            리뷰 ID: {id}
          </Typography>
        </Paper>
      </Grid>
    </Grid>
  );
};

export default ReviewDetailPage; 